package com.mykino.config;

import com.mykino.entity.User;
import com.mykino.enums.AuthProvider;
import com.mykino.enums.UserRole;
import com.mykino.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerId;
        String email;
        String nickname;
        String profileImage;
        AuthProvider provider;

        if ("google".equals(registrationId)) {
            provider = AuthProvider.GOOGLE;
            providerId = (String) attributes.get("sub");
            email = (String) attributes.get("email");
            nickname = (String) attributes.get("name");
            profileImage = (String) attributes.get("picture");
        } else if ("kakao".equals(registrationId)) {
            provider = AuthProvider.KAKAO;
            providerId = String.valueOf(attributes.get("id"));

            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            email = (String) kakaoAccount.get("email");
            nickname = (String) profile.get("nickname");
            profileImage = (String) profile.get("profile_image_url");
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다: " + registrationId);
        }

        Optional<User> existingUser = userRepository.findByProviderAndProviderId(provider, providerId);

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            // 닉네임 중복 방지
            String finalNickname = nickname;
            if (finalNickname == null || finalNickname.isBlank()) {
                finalNickname = provider.name().toLowerCase() + "_" + providerId.substring(0, Math.min(6, providerId.length()));
            }
            if (userRepository.existsByNickname(finalNickname)) {
                finalNickname = finalNickname + "_" + UUID.randomUUID().toString().substring(0, 4);
            }

            // 이메일 중복 방지 (소셜 이메일이 이미 로컬 계정으로 존재할 수 있음)
            String finalEmail = email;
            if (finalEmail == null || finalEmail.isBlank()) {
                finalEmail = provider.name().toLowerCase() + "_" + providerId + "@mykino.social";
            }
            if (userRepository.existsByEmail(finalEmail)) {
                finalEmail = provider.name().toLowerCase() + "_" + providerId + "@mykino.social";
            }

            user = User.builder()
                    .email(finalEmail)
                    .nickname(finalNickname)
                    .profileImage(profileImage)
                    .provider(provider)
                    .providerId(providerId)
                    .role(UserRole.USER)
                    .build();
            userRepository.save(user);
        }

        return new CustomUserDetails(user, attributes);
    }
}
