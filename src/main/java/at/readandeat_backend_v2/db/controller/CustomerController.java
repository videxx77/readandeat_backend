package at.readandeat_backend_v2.db.controller;

import at.readandeat_backend_v2.db.fileupload.FileUploadUtil;
import at.readandeat_backend_v2.db.models.Customer;
import at.readandeat_backend_v2.db.models.User;
import at.readandeat_backend_v2.db.payload.request.CustomerRequest;
import at.readandeat_backend_v2.db.payload.response.MessageResponse;
import at.readandeat_backend_v2.db.repositories.CustomerRepository;
import at.readandeat_backend_v2.db.repositories.UserRepository;
import at.readandeat_backend_v2.db.security.services.UserDetailsImpl;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    public ResponseEntity<?> addCustomer(@Valid @ModelAttribute CustomerRequest customerRequest) {
        UserDetailsImpl userDetails = getUserDeatailImpl();

        Customer customer = new Customer(
                customerRequest.getFirstName(),
                customerRequest.getLastName(),
                customerRequest.getBalance()
        );

        //ordnet den eingeloggten User dem neuen Customer zu
        customer.setUser(userRepository.findByUserID(userDetails.getUserID()).orElse(null));
        customerRepository.save(customer);

        System.out.println(customerRequest.getImage());

        try
        {
            //file upload
            MultipartFile image = customerRequest.getImage();
            String fileName = customer.getCustomerID() + "." + image.getOriginalFilename().split("\\.")[1];
            String uploadDir = "customer-photos/"+ userDetails.getUserID();
            String apiDir = "customer/" + uploadDir;
            uploadDir = "src/main/resources/" + uploadDir;
            try
            {
                FileUploadUtil.saveFile(uploadDir, fileName, image);

            } catch (IOException e)
            {
                return ResponseEntity.internalServerError().body(new MessageResponse("File upload didnt work!"));
            }

            customer.setPictureURL(apiDir + "/" + fileName);

        }catch (ArrayIndexOutOfBoundsException e)
        {
            customer.setPictureURL(null);
        }
        customerRepository.save(customer);

        return ResponseEntity.ok(new MessageResponse("Customer registered successfully!"));
    }

    @DeleteMapping(path = "/delete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteCustomer(@RequestParam(name = "id") long id) {
        UserDetailsImpl userDetails = getUserDeatailImpl();

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

        try
        {
            String filepath = "/customer-photos/" + userDetails.getUserID() + "/" +
                    customer.getPictureURL().split("/")[3];
            File f= new File("src/main/resources/" + filepath);
            f.delete();
        } catch (Exception e)
        {
            return ResponseEntity.ok(new MessageResponse("Customer "+
                    customer.getFirstName() +
                    " " +
                    customer.getLastName() +
                    " was deleted successfully but no picture was found!"));
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
    public ResponseEntity<?> updateCustomer(@Valid @ModelAttribute CustomerRequest customerRequest, @RequestParam(name = "id") long id) {
        UserDetailsImpl userDetails = getUserDeatailImpl();

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

        customer.setFirstName(customerRequest.getFirstName() != null ? customerRequest.getFirstName() : customer.getFirstName());
        customer.setLastName(customerRequest.getLastName() != null ? customerRequest.getLastName() : customer.getLastName());

        if(customerRequest.getImage() != null)
        {
            //file upload
            MultipartFile image = customerRequest.getImage();
            String fileName = customer.getCustomerID() + "." + image.getOriginalFilename().split("\\.")[1];
            String uploadDir = "customer-photos/"+ userDetails.getUserID();
            String apiDir = "customer/" + uploadDir;
            uploadDir = "src/main/resources/" + uploadDir;
            try
            {
                FileUploadUtil.saveFile(uploadDir, fileName, image);

                String filepath = "/customer-photos/" + userDetails.getUserID() + "/" +
                        customer.getPictureURL().split("/")[3];
                File f = new File("src/main/resources/" + filepath);
                f.delete();

            } catch (IOException e)
            {
                return ResponseEntity.internalServerError().body(new MessageResponse("File upload didnt work!"));
            }



            customer.setPictureURL(apiDir + "/" + fileName);

        }
        else
        {
            //picture url gleich lassen
        }

        customerRepository.save(customer);

        return ResponseEntity.ok(new MessageResponse("Customer "+
                customer.getFirstName() +
                " " +
                customer.getLastName() +
                " updated successfully!"));
    }

    @PatchMapping(path = "/raiseFounds")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> raiseFounds(@RequestParam(name = "id") long id, @RequestParam(name = "raise") double raise) {
        UserDetailsImpl userDetails = getUserDeatailImpl();

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
    public ResponseEntity<List<Customer>> getAllCustomers() {
        UserDetailsImpl userDetails = getUserDeatailImpl();

        //sucht nach allen customers die dem User der gerade eingeloggt ist zugewiesen sind
        List<Customer> customerList = customerRepository.findCustomersByUser(userRepository.findByUserID(userDetails.getUserID()).orElse(null));

        return ResponseEntity.ok(customerList);
    }

    @GetMapping(path = "/getById")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getCustomerByID(@RequestParam(name = "id") long id) {
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

    @GetMapping(
            path = "/customer-photos/{user}/{image}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    @PreAuthorize("hasRole('USER')")
    public @ResponseBody ResponseEntity<?> getImage(@PathVariable(name = "user") long userId, @PathVariable(name = "image") String image)
    {
        UserDetailsImpl userDetails = getUserDeatailImpl();

        String filepath = "/customer-photos/" + userDetails.getUserID() + "/" + image;


        InputStream in = getClass()
                .getResourceAsStream(filepath);
        if(in == null)
        {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "File with path "+filepath+" not found"
            );
        }

        try
        {
            byte[] out = IOUtils.toByteArray(in);
            return ResponseEntity.ok(out);
        } catch (IOException e)
        {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Ops something went wrong"
            );
        }
    }

    /**
     *  findet Customer mit der Id des Users + den Customer aber nur wenn er dem User zugeordnet ist
     */
    public Customer getCostumerById(long id) throws FileNotFoundException
    {
        UserDetailsImpl userDetails = getUserDeatailImpl();

        User user = userRepository.findByUserID(userDetails.getUserID()).orElse(null);

        if(!customerRepository.existsByCustomerIDAndUser(id, user))
        {
            throw new FileNotFoundException();
        }

        return customerRepository.findByCustomerIDAndUser(id,
                user)
                .orElse(null);
    }

    public UserDetailsImpl getUserDeatailImpl ()
    {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
