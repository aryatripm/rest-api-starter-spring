package id.arya.portofolio.users.user;

import id.arya.portofolio.users.auth.AuthService;
import id.arya.portofolio.users.config.WebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    @GetMapping(
            path = "/current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> current(Principal user) {
        UserResponse userResponse = userService.get(user.getName());
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }

    @PatchMapping("/change-password")
    public WebResponse<ChangePasswordResponse> changePassword(@RequestBody ChangePasswordRequest request, Principal user) {
        ChangePasswordResponse response = authService.changePassword(request, user);
        return WebResponse.<ChangePasswordResponse>builder().data(response).build();
    }

    @GetMapping(
            path = "/",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
//    @PreAuthorize("hasAnyRole('ADMIN')")
    public WebResponse<UsersResponse> findAll() {
        UsersResponse responses = userService.getAll();
        return WebResponse.<UsersResponse>builder().data(responses).build();
    }

    @GetMapping(
            path = "/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> findByUsername(@PathVariable String username) {
        UserResponse responses = userService.get(username);
        return WebResponse.<UserResponse>builder().data(responses).build();
    }
}
