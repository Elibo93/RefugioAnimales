package es.refugio.auth.social_login.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SocialUser {
    String email;
    String name;
    String picture;
}
