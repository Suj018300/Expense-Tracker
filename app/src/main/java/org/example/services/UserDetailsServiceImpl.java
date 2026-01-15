package org.example.services;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.entities.UserInfo;
import org.example.model.UserInfoDto;
import org.example.repository.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;


@Component
@AllArgsConstructor
@Data
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    private static final Logger log = (Logger) LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername (String username) throws UsernameNotFoundException {
        UserInfo user = userRepository.findByUsername(username);
        if (user == null) {
//            log.error("User not found: " + username);
            throw new UsernameNotFoundException("Could not found user..!");
        }
        log.info("User Authenticated successfully");
        return new CustomUserDetail(user);
    }

    public UserInfo checkIfUserAlreadyExits(UserInfoDto userInfoDto) {
        return userRepository.findByUsername(userInfoDto.getUsername());
    }

    public Boolean signupUser (UserInfoDto userInfoDto) {
        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));
        if(Objects.nonNull(checkIfUserAlreadyExits(userInfoDto))) {
            return false;
        }
        String userId = UUID.randomUUID().toString();
        userRepository.save(new UserInfo(
                userId,
                userInfoDto.getUsername(),
                userInfoDto.getPassword(),
                new HashSet<>()
        ));
        return true;
    }
}
