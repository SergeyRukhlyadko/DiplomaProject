package org.diploma.app.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.controller.request.post.RequestCommentBody;
import org.diploma.app.controller.request.post.RequestModerationBody;
import org.diploma.app.controller.request.post.RequestProfileBody;
import org.diploma.app.controller.response.BadRequestBody;
import org.diploma.app.controller.response.DefaultBody;
import org.diploma.app.controller.response.ErrorBody;
import org.diploma.app.controller.response.ResponseBadRequestBody;
import org.diploma.app.controller.response.ResponseCalendarBody;
import org.diploma.app.controller.response.ResponseStatisticBody;
import org.diploma.app.controller.response.ResponseTagBody;
import org.diploma.app.dto.TagDto;
import org.diploma.app.model.db.entity.PostsCountByTagName;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.db.entity.enumeration.GlobalSetting;
import org.diploma.app.model.db.entity.projection.PostVotesStatistics;
import org.diploma.app.model.db.entity.projection.PostsStatistics;
import org.diploma.app.service.AuthService;
import org.diploma.app.service.CheckupService;
import org.diploma.app.service.GeneralService;
import org.diploma.app.service.PostService;
import org.diploma.app.util.NormalizationAlgorithm;
import org.diploma.app.util.NullRemover;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api")
class ApiGeneralController {

    String title;
    String subtitle;
    String phone;
    String email;
    String copyright;
    String copyrightFrom;
    ApplicationContext context;
    AuthService authService;
    GeneralService generalService;
    PostService postService;

    public ApiGeneralController(
        @Value("${init.title}") String title,
        @Value("${init.subtitle}") String subtitle,
        @Value("${init.phone}") String phone,
        @Value("${init.email}") String email,
        @Value("${init.copyright}") String copyright,
        @Value("${init.copyrightFrom}") String copyrightFrom,
        ApplicationContext context,
        AuthService authService,
        GeneralService generalService,
        PostService postService
    ) {
        this.title = title;
        this.subtitle = subtitle;
        this.phone = phone;
        this.email = email;
        this.copyright = copyright;
        this.copyrightFrom = copyrightFrom;
        this.context = context;
        this.authService = authService;
        this.generalService = generalService;
        this.postService = postService;
    }

    //Метод смены флага модератора для удобства
    @GetMapping("/moderator")
    ResponseEntity<?> moderator(@RequestParam String email) {
        try {
            generalService.changeModeratorStatus(email);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(400).body(
                new BadRequestBody("Пользователь " + email + " не найден"));
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/init")
    Map<String, String> init() {
        Map<String, String> initMap = new HashMap<>();
        initMap.put("title", title);
        initMap.put("subtitle", subtitle);
        initMap.put("phone", phone);
        initMap.put("email", email);
        initMap.put("copyright", copyright);
        initMap.put("copyrightFrom", copyrightFrom);
        return initMap;
    }

    @GetMapping("/tag")
    ResponseEntity<?> tag(@RequestParam(required = false) String query) {
        List<PostsCountByTagName> postsCountByTagNameList = generalService.getAllTags();
        List<TagDto> tagDtoList = new ArrayList<>();
        if (postsCountByTagNameList.size() == 0) {
            return ResponseEntity.ok(new ResponseTagBody(tagDtoList));
        }

        long postsCount = postService.postsCount();
        double min = (float) postsCountByTagNameList.get(0).getPostsCount() / postsCount;
        double max = min;
        Map<String, Double> weights = new HashMap<>();
        weights.put(postsCountByTagNameList.get(0).getName(), min);
        for (int i = 1; i < postsCountByTagNameList.size(); i++) {
            double weight = (float) postsCountByTagNameList.get(i).getPostsCount() / postsCount;
            if (min > weight) {
                min = weight;
            } else if (max < weight) {
                max = weight;
            }

            weights.put(postsCountByTagNameList.get(i).getName(), weight);
        }

        if (query == null || query.isEmpty()) {
            for (Map.Entry<String, Double> entry : weights.entrySet()) {
                tagDtoList.add(
                    new TagDto(entry.getKey(),
                        NormalizationAlgorithm.normalizeMinMax(min, max, entry.getValue())));
            }
        } else {
            if (weights.containsKey(query)) {
                tagDtoList.add(
                    new TagDto(query,
                        NormalizationAlgorithm.normalizeMinMax(min, max, weights.get(query))));
            }
        }

        return ResponseEntity.ok(new ResponseTagBody(tagDtoList));
    }

    @PostMapping("/comment")
    ResponseEntity<?> comment(Principal principal, @RequestBody RequestCommentBody requestBody) {
        CheckupService checkupService = context.getBean("checkupService", CheckupService.class);
        checkupService.comment(requestBody.getText());
        Map<String, String> errors = checkupService.getErrors();

        if (!errors.isEmpty()) {
            return ResponseEntity.ok(new ErrorBody(errors));
        }

        try {
            int id = generalService.addComment(
                principal.getName(), requestBody.getParentId(), requestBody.getPostId(), requestBody.getText());
            Map<String, Integer> response = new HashMap<>();
            response.put("id", id);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(400).body(
                new BadRequestBody("Соответствующие комментарий и/или пост не существуют"));
        }
    }

    @PostMapping("moderation")
    DefaultBody moderation(Principal principal, @RequestBody RequestModerationBody requestBody) {
        return new DefaultBody(generalService.changeModerationStatus(
            principal.getName(), requestBody.getPostId(), requestBody.getDecision()));
    }

    @PostMapping("/image")
    ResponseEntity<?> image(@RequestParam("image") MultipartFile multipartFile) throws IOException {
        ImageInputStream iis = ImageIO.createImageInputStream(multipartFile.getInputStream());
        Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
        String format;
        if (imageReaders.hasNext()) {
            format = imageReaders.next().getFormatName();
        } else {
            return ResponseEntity.status(400).body(new BadRequestBody("Файл не найден"));
        }

        CheckupService checkupService = context.getBean("checkupService", CheckupService.class);
        checkupService.imageSize(multipartFile.getSize());
        checkupService.imageFormat(format);
        Map<String, String> errors = checkupService.getErrors();
        if (!errors.isEmpty()) {
            return ResponseEntity.ok(new ErrorBody(errors));
        }

        return ResponseEntity.ok("/" + generalService.uploadImage(multipartFile.getBytes(), format));
    }

    @PostMapping(value = "/profile/my", consumes = "application/json")
    ResponseEntity<?> profileMy(HttpSession session, Principal principal, @RequestBody RequestProfileBody requestBody) {
        CheckupService checkupService = context.getBean("checkupService", CheckupService.class);
        checkupService.name(requestBody.getName())
            .password(requestBody.getPassword())
            .removePhoto(requestBody.getPhoto(), requestBody.getRemovePhoto());

        if (!principal.getName().equals(requestBody.getEmail())) {
            checkupService.email(requestBody.getEmail());
        }

        Map<String, String> errors = checkupService.getErrors();
        if (!errors.isEmpty()) {
            return ResponseEntity.ok(new ErrorBody(errors));
        }

        boolean isUpdated = generalService.updateProfile(
            principal.getName(),
            requestBody.getName(),
            requestBody.getEmail(),
            requestBody.getPassword(),
            requestBody.getPhoto()
        );

        return ResponseEntity.ok(new DefaultBody(isUpdated));
    }

    @PostMapping(value = "/profile/my", consumes = "multipart/form-data")
    ResponseEntity<?> profileMy(
        HttpSession session, Principal principal,
        @RequestParam("photo") MultipartFile multipartFile,
        @RequestParam(value = "removePhoto", required = false) int removePhoto,
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "email", required = false) String email,
        @RequestParam(value = "password", required = false) String password
    ) throws IOException {
        ImageInputStream iis = ImageIO.createImageInputStream(multipartFile.getInputStream());
        Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
        String format;
        if (imageReaders.hasNext()) {
            format = imageReaders.next().getFormatName();
        } else {
            return ResponseEntity.status(400).body(new BadRequestBody("Файл не найден"));
        }

        CheckupService checkupService = context.getBean("checkupService", CheckupService.class);
        checkupService.changePhoto(removePhoto)
            .imageSize(multipartFile.getSize())
            .imageFormat(format)
            .name(name)
            .password(password);

        if (!principal.getName().equals(email)) {
            checkupService.email(email);
        }

        Map<String, String> errors = checkupService.getErrors();
        if (!errors.isEmpty()) {
            return ResponseEntity.ok(new ErrorBody(errors));
        }

        BufferedImage originalImage = ImageIO.read(iis);
        BufferedImage outputImage = Scalr.resize(originalImage, 36, 36);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(outputImage, format, baos);

        String photoPath = "/" + generalService.uploadImage(baos.toByteArray(), format);
        boolean isUpdated = generalService.updateProfile(principal.getName(), name, email, password, photoPath);

        return ResponseEntity.ok(new DefaultBody(isUpdated));
    }

    @GetMapping("/calendar")
    ResponseCalendarBody calendar(@RequestParam(required = false) Integer year) {
        return new ResponseCalendarBody(
            generalService.years(),
            generalService.countByYear(Objects.requireNonNullElseGet(year, () -> LocalDateTime.now().getYear()))
        );
    }

    @GetMapping("/statistics/all")
    ResponseEntity<?> statisticsAll(HttpSession session) {
        if (!generalService.isEnabled(GlobalSetting.STATISTICS_IS_PUBLIC)) {
            Users user = authService.checkAuthentication();
            if (!user.isModerator()) {
                return ResponseEntity.status(401).build();
            }
        }

        PostsStatistics postsStatistics = generalService.getPostStatistics();
        PostVotesStatistics postVotesStatistics = generalService.getPostVoteStatistics();

        return ResponseEntity.ok(new ResponseStatisticBody(
            postsStatistics.getPostsCount(),
            postVotesStatistics.getLikesCount(),
            postVotesStatistics.getDislikesCount(),
            postsStatistics.getViewsCount(),
            postsStatistics.getFirstPublication()
        ));
    }

    @GetMapping("/statistics/my")
    ResponseStatisticBody statisticsMy(Principal principal) {
        PostsStatistics postsStatistics = generalService.getPostStatistics(principal.getName());
        PostVotesStatistics postVotesStatistics = generalService.getPostVotesStatistics(principal.getName());
        return new ResponseStatisticBody(
            postsStatistics.getPostsCount(),
            postVotesStatistics.getLikesCount(),
            postVotesStatistics.getDislikesCount(),
            postsStatistics.getViewsCount(),
            postsStatistics.getFirstPublication()
        );
    }

    @GetMapping("/settings")
    Map<String, Boolean> settings() {
        return generalService.findAllGlobalSettings();
    }

    @PutMapping("/settings")
    ResponseEntity<?> settings(Principal principal, @RequestBody HashMap<String, Boolean> settings) {
        boolean isModerator = authService.isModerator(principal.getName());
        if (!isModerator) {
            return ResponseEntity.status(400)
                .body(new ResponseBadRequestBody("Пользователь не модератор"));
        }

        NullRemover.remove(settings);
        if (settings.isEmpty()) {
            return ResponseEntity.ok().build();
        }

        generalService.updateGlobalSettings(settings);
        return ResponseEntity.ok().build();
    }
}