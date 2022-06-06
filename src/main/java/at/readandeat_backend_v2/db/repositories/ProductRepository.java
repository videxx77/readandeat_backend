package at.readandeat_backend_v2.db.repositories;

import at.readandeat_backend_v2.db.models.Product;
import at.readandeat_backend_v2.db.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>
{
    List<Product> findProductsByUser(User user);
    Optional<Product> findProductByProductIDAndUser(long productID, User user);

    boolean existsByProductIDAndUser(long productID, User user);


}
