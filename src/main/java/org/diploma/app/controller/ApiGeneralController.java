package org.diploma.app.controller;

import org.diploma.app.controller.request.RequestCommentBody;
import org.diploma.app.controller.request.RequestProfileBody;
import org.diploma.app.controller.request.post.RequestModerationBody;
import org.diploma.app.controller.response.BadRequestBody;
import org.diploma.app.controller.response.DefaultBody;
import org.diploma.app.controller.response.ErrorBody;
import org.diploma.app.controller.response.ResponseCalendarBody;
import org.diploma.app.controller.response.ResponseErrorBody;
import org.diploma.app.controller.response.ResponseTagBody;
import org.diploma.app.dto.TagDto;
import org.diploma.app.service.CheckupService;
import org.diploma.app.service.GeneralService;
import org.diploma.app.service.TagStatisticService;
import org.diploma.app.service.UserService;
import org.diploma.app.validation.ValidationOrder;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
class ApiGeneralController {

    private String title;
    private String subtitle;
    private String phone;
    private String email;
    private String copyright;
    private String copyrightFrom;
    private ApplicationContext context;
    private GeneralService generalService;
    private UserService userService;
    private MessageSource messageSource;
    private TagStatisticService tagStatisticService;

    public ApiGeneralController(
        @Value("${init.title}") String title,
        @Value("${init.subtitle}") String subtitle,
        @Value("${init.phone}") String phone,
        @Value("${init.email}") String email,
        @Value("${init.copyright}") String copyright,
        @Value("${init.copyrightFrom}") String copyrightFrom,
        ApplicationContext context,
        GeneralService generalService,
        UserService userService,
        MessageSource messageSource,
        TagStatisticService tagStatisticService
    ) {
        this.title = title;
        this.subtitle = subtitle;
        this.phone = phone;
        this.email = email;
        this.copyright = copyright;
        this.copyrightFrom = copyrightFrom;
        this.context = context;
        this.generalService = generalService;
        this.userService = userService;
        this.messageSource = messageSource;
        this.tagStatisticService = tagStatisticService;
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
        List<TagDto> tagDtoList = new ArrayList<>();
        if (query == null || query.isBlank()) {
            Map<String, Float> normalizedWeights = tagStatisticService.getAllNormalizedWeight();
            normalizedWeights.forEach((k, v) -> tagDtoList.add(new TagDto(k, v)));
        } else {
            float normalizedWeight = tagStatisticService.getNormalizedWeight(query);
            tagDtoList.add(new TagDto(query, normalizedWeight));
        }

        return ResponseEntity.ok(new ResponseTagBody(tagDtoList));
    }

    @PostMapping("/comment")
    ResponseEntity<?> addComment(Principal principal, @Valid @RequestBody RequestCommentBody body) {
        try {
            int id = generalService.addComment(
                principal.getName(), body.getParentId(), body.getPostId(), body.getText());
            return ResponseEntity.ok(Collections.singletonMap("id", id));
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

    @PostMapping(value = "/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> changeProfile(
        Principal principal, @Validated(ValidationOrder.class) @RequestBody RequestProfileBody body, Locale locale
    ) throws IOException {
        if (userService.exists(body.getEmail())) {
            return ResponseEntity.ok(
                new ResponseErrorBody("email", messageSource.getMessage("email.exists.message", null, locale)));
        }

        generalService.updateProfile(
            principal.getName(),
            body.getName(),
            body.getEmail(),
            body.getPassword(),
            body.getPhoto()
        );

        //TODO forward to login
        return ResponseEntity.ok(new DefaultBody(true));
    }

    @PostMapping(value = "/profile/my", consumes = "multipart/form-data")
    ResponseEntity<?> profileMy(
        Principal principal,
        @RequestParam("photo") MultipartFile multipartFile,
        @RequestParam(value = "removePhoto", required = false) int removePhoto,
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "email", required = false) String email,
        @RequestParam(value = "password", required = false) String password,
        Locale locale
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

        Map<String, String> errors = checkupService.getErrors();
        if (!errors.isEmpty()) {
            return ResponseEntity.ok(new ErrorBody(errors));
        }

        if (userService.exists(email)) {
            return ResponseEntity.ok(
                new ResponseErrorBody("email", messageSource.getMessage("email.exists.message", null, locale)));
        }

        BufferedImage originalImage = ImageIO.read(iis);
        BufferedImage outputImage = Scalr.resize(originalImage, 36, 36);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(outputImage, format, baos);

        String photoPath = "/" + generalService.uploadImage(baos.toByteArray(), format);
        generalService.updateProfile(principal.getName(), name, email, password, photoPath);

        //TODO forward to login
        return ResponseEntity.ok(new DefaultBody(true));
    }

    @GetMapping("/calendar")
    ResponseCalendarBody calendar(@RequestParam(required = false) Integer year) {
        return new ResponseCalendarBody(
            generalService.years(),
            generalService.countByYear(Objects.requireNonNullElseGet(year, () -> LocalDateTime.now().getYear()))
        );
    }
}