package study.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.account.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
