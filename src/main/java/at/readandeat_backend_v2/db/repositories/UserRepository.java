package at.readandeat_backend_v2.db.repositories;

import at.readandeat_backend_v2.db.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUserID(long userID);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}