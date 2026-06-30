package com.gyu.engdu.domain.user.domain;

import com.gyu.engdu.domain.auth.domain.OAuthProvider;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByProviderAndSub(OAuthProvider provider, String sub);
}