package at.readandeat_backend_v2.db.controller;

import at.readandeat_backend_v2.db.models.Customer;
import at.readandeat_backend_v2.db.models.Product;
import at.readandeat_backend_v2.db.models.User;
import at.readandeat_backend_v2.db.payload.request.CustomerRequest;
import at.readandeat_backend_v2.db.payload.request.SignupRequest;
import at.readandeat_backend_v2.db.payload.response.MessageResponse;
import at.readandeat_backend_v2.db.repositories.CustomerRepository;
import at.readandeat_backend_v2.db.repositories.UserRepository;
import at.readandeat_backend_v2.db.security.services.UserDetailsImpl;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PostUpdate;
import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/customer")
public class CustomerController
{
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping(path = "/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addCustomer(Authentication auth, @Valid @RequestBody CustomerRequest customerRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        Customer customer = new Customer(
                customerRequest.getFirstName(),
                customerRequest.getLastName(),
                customerRequest.getBalance(),
                customerRequest.getPictureURL()
        );

        //ordnet den eingeloggten User dem neuen Customer zu
        customer.setUser(userRepository.findByUserID(userDetails.getUserID()).orElse(null));

        customerRepository.save(customer);

        return ResponseEntity.ok(new MessageResponse("Customer registered successfully!"));
    }

    @DeleteMapping(path = "/delete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteCustomer(Authentication auth, @RequestParam(name = "id") long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        Customer customer;
        try
        {
            customer = this.getCostumerById(id);
        } catch (FileNotFoundException fileNotFoundException)
        {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: No customer with this id for this user"));
        }

        customerRepository.delete(customer);

        return ResponseEntity.ok(new MessageResponse("Customer "+
                customer.getFirstName() +
                " " +
                customer.getLastName() +
                " was deleted successfully!"));
    }

    @PatchMapping(path = "/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateCustomer(Authentication auth, @Valid @RequestBody CustomerRequest customerRequest, @RequestParam(name = "id") long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        Customer customer;
        try
        {
            customer = this.getCostumerById(id);
        } catch (FileNotFoundException fileNotFoundException)
        {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: No customer with this id for this user"));
        }

        customer.setBalance(customerRequest.getBalance() != Double.MAX_VALUE ? customerRequest.getBalance() : customer.getBalance());
        customer.setFirstName(customerRequest.getFirstName() != null ? customerRequest.getFirstName() : customer.getFirstName());
        customer.setLastName(customerRequest.getLastName() != null ? customerRequest.getLastName() : customer.getLastName());
        customer.setPictureURL(customerRequest.getPictureURL() != null ? customerRequest.getPictureURL() : customer.getPictureURL());

        customerRepository.save(customer);

        return ResponseEntity.ok(new MessageResponse("Customer "+
                customer.getFirstName() +
                " " +
                customer.getLastName() +
                " updated successfully!"));
    }

    @PatchMapping(path = "/raiseFounds")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> raiseFounds(Authentication auth, @RequestParam(name = "id") long id, @RequestParam(name = "raise") double raise) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        Customer customer;
        try
        {
            customer = this.getCostumerById(id);
        } catch (FileNotFoundException fileNotFoundException)
        {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: No customer with this id for this user"));
        }

        customer.setBalance(customer.getBalance()+raise);

        customerRepository.save(customer);

        return ResponseEntity.ok(new MessageResponse("Customers founds changed. New balance of "+
                customer.getFirstName() +
                " " +
                customer.getLastName() +
                " : " +
                customer.getBalance()));
    }

    @GetMapping(path = "/all")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Customer>> getAllCustomers(Authentication auth) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        //sucht nach allen customers die dem User der gerade eingeloggt ist zugewiesen sind
        List<Customer> customerList = customerRepository.findCustomersByUser(userRepository.findByUserID(userDetails.getUserID()).orElse(null));

        return ResponseEntity.ok(customerList);
    }

    @GetMapping(path = "/getById")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getCustomerByID(Authentication auth, @RequestParam(name = "id") long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        Customer customer;
        try
        {
            customer = this.getCostumerById(id);
        } catch (FileNotFoundException fileNotFoundException)
        {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: No customer with this id for this user"));
        }

        return ResponseEntity.ok(customer);
    }

    //findet Customer mit der Id des Users + den Customer aber nur wenn er dem User zugeordnet ist
    public Customer getCostumerById(long id) throws FileNotFoundException
    {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        User user = userRepository.findByUserID(userDetails.getUserID()).orElse(null);

        if(!customerRepository.existsByCustomerIDAndUser(id, user))
        {
            throw new FileNotFoundException();
        }

        return customerRepository.findByCustomerIDAndUser(id,
                user)
                .orElse(null);
    }
}
