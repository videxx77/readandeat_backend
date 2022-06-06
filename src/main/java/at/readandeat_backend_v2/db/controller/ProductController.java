package at.readandeat_backend_v2.db.controller;

import at.readandeat_backend_v2.db.models.Customer;
import at.readandeat_backend_v2.db.models.Product;
import at.readandeat_backend_v2.db.models.User;
import at.readandeat_backend_v2.db.payload.request.CustomerRequest;
import at.readandeat_backend_v2.db.payload.request.ProductRequest;
import at.readandeat_backend_v2.db.payload.response.MessageResponse;
import at.readandeat_backend_v2.db.repositories.ProductRepository;
import at.readandeat_backend_v2.db.repositories.UserRepository;
import at.readandeat_backend_v2.db.security.services.UserDetailsImpl;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.hibernate.annotations.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/product")
public class ProductController
{
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping(path = "/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addProduct(Authentication auth, @Valid @RequestBody ProductRequest productRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        Product product = new Product(
                productRequest.getName(),
                productRequest.getPrice(),
                productRequest.getPictureURL()
        );

        //ordnet den eingeloggten User dem neuen Product zu
        product.setUser(userRepository.findByUserID(userDetails.getUserID()).orElse(null));

        productRepository.save(product);

        return ResponseEntity.ok(new MessageResponse("Product added successfully!"));
    }

    @DeleteMapping(path = "/delete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteCustomer(Authentication auth, @RequestParam(name = "id") long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        Product product;
        try
        {
            product = this.getProductById(id);
        } catch (FileNotFoundException fileNotFoundException)
        {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: No product with this id for this user"));
        }


        productRepository.delete(product);

        return ResponseEntity.ok(new MessageResponse("Product "+
                product.getName() +
                " was deleted successfully!"));
    }

    @PatchMapping(path = "/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateCustomer(Authentication auth, @Valid @RequestBody ProductRequest productRequest, @RequestParam(name = "id") long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();


        Product product;
        try
        {
            product = this.getProductById(id);
        } catch (FileNotFoundException fileNotFoundException)
        {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: No customer with this id for this user"));
        }

        product.setPrice(productRequest.getPrice() != Double.MAX_VALUE ? productRequest.getPrice() : product.getPrice() );
        product.setName(productRequest.getName() != null ? productRequest.getName() : product.getName());
        product.setPictureURL(productRequest.getPictureURL() != null ? productRequest.getPictureURL() : product.getPictureURL());

        productRepository.save(product);

        return ResponseEntity.ok(new MessageResponse("Product "+
                product.getName() +
                " updated successfully!"));
    }

    @GetMapping(path = "/all")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Product>> getAllProduct(Authentication auth)
    {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        List<Product> productList = productRepository.findProductsByUser(userRepository.findByUserID(userDetails.getUserID()).orElse(null));

        return ResponseEntity.ok(productList);
    }

    @GetMapping(path = "/getById")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getProductByID(Authentication auth, @RequestParam(name = "id") long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        Product product;
        try
        {
            product = this.getProductById(id);
        } catch (FileNotFoundException fileNotFoundException)
        {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: No product with this id for this user"));
        }

        return ResponseEntity.ok(product);
    }


    //findet Product mit der Id des Users + das Product aber nur wenn er dem User zugeordnet ist
    public Product getProductById(long id) throws FileNotFoundException
    {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        User user = userRepository.findByUserID(userDetails.getUserID()).orElse(null);

        if(!productRepository.existsByProductIDAndUser(id, user))
        {
            throw new FileNotFoundException();
        }

        return productRepository.findProductByProductIDAndUser(id,
                user)
                .orElse(null);
    }
}
