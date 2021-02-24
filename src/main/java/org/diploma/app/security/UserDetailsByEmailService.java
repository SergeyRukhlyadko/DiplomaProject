package org.diploma.app.security;

import org.diploma.app.model.db.entity.Users;
import org.diploma.app.repository.UsersRepository;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsByEmailService implements UserDetailsService {

    private UsersRepository usersRepository;

    public UserDetailsByEmailService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //TODO get projection of email and password only
        Users user = usersRepository.findByEmail(email).orElseThrow(
            () -> new UsernameNotFoundException("User " + email + " not found")
        );

        return new User(user.getEmail(), user.getPassword(), AuthorityUtils.NO_AUTHORITIES);
    }
}
