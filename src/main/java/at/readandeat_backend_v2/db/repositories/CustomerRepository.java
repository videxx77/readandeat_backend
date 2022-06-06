package at.readandeat_backend_v2.db.repositories;

import at.readandeat_backend_v2.db.models.Customer;
import at.readandeat_backend_v2.db.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>
{
    Optional<Customer> findByCustomerIDAndUser(long customerID, User user);
    List<Customer> findCustomersByUser(User user);

    Boolean existsByCustomerIDAndUser(long customerID, User user);
}
