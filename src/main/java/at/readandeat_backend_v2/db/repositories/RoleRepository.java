package at.readandeat_backend_v2.db.repositories;

import at.readandeat_backend_v2.db.models.ERole;
import at.readandeat_backend_v2.db.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>
{
    Optional<Role> findByName(ERole name);
}
