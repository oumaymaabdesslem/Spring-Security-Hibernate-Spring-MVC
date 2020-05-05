package com.example.springboot.controller;

import com.example.springboot.entity.Role;
import com.example.springboot.entity.User;
import com.example.springboot.exception.CustomeFieldValidationException;
import com.example.springboot.mdp.ChangePasswordForm;
import com.example.springboot.repository.RoleRepository;
import com.example.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Controller
public class UserController {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserService userService;

    @GetMapping({"/","/login"})
    public String index() {
        return "index";
    }


    @GetMapping("/signup")
    public String signup(Model model) {
        Role userRole = roleRepository.findByName("USER");
        List<Role> roles = Arrays.asList(userRole);

        model.addAttribute("signup",true);
        model.addAttribute("userForm", new User());
        model.addAttribute("roles",roles);
        return "user-form/user-signup";
    }
    @PostMapping("/signup")
    public String signupAction(@Valid @ModelAttribute("userForm")User user, BindingResult result, ModelMap model) {
        Role userRole = roleRepository.findByName("USER");
        List<Role> roles = Arrays.asList(userRole);
        model.addAttribute("userForm", user);
        model.addAttribute("roles",roles);
        model.addAttribute("signup",true);

        if(result.hasErrors()) {
            return "user-form/user-signup";
        }else {
            try {
                userService.createUser(user);
            } catch (CustomeFieldValidationException cfve) {
                result.rejectValue(cfve.getFieldName(), null, cfve.getMessage());
            }catch (Exception e) {
                model.addAttribute("formErrorMessage",e.getMessage());
            }
        }
        return index();
    }

    @GetMapping("/userForm")
    public String getUserForm(Model model) {
        model.addAttribute("userForm", new User());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("userList", userService.getAllUsers());
        model.addAttribute("listTab", "active");
        return "user-form/user-view";
    }

    @PostMapping("/userForm")
    public String createUser(@Valid @ModelAttribute("userForm") User user, BindingResult result, ModelMap model) {
        if (result.hasErrors()) {
            model.addAttribute("userForm", user);
            model.addAttribute("formTab", "active");
        } else {
            try {
                userService.createUser(user);
                model.addAttribute("userForm", new User());
                model.addAttribute("listTab", "active");

            }catch (CustomeFieldValidationException cfve) {
                result.rejectValue(cfve.getFieldName(), null, cfve.getMessage());
                model.addAttribute("userForm", user);
                model.addAttribute("formTab","active");
                model.addAttribute("userList", userService.getAllUsers());
                model.addAttribute("roles",roleRepository.findAll());
            }
            catch (Exception e) {
                model.addAttribute("formErrorMessage", e.getMessage());
                model.addAttribute("userForm", new User());
                model.addAttribute("formTab", "active");
                model.addAttribute("roles", roleRepository.findAll());
                model.addAttribute("userList", userService.getAllUsers());
            }

        }
        model.addAttribute("userList", userService.getAllUsers());
        model.addAttribute("roles", roleRepository.findAll());
        return "user-form/user-view";
    }

    @GetMapping("/editUser/{id}")
    public String getEditUserForm(Model model, @PathVariable(name = "id") Long id) throws Exception {
        User user = userService.getUserById(id);
        model.addAttribute("userForm", user);
        model.addAttribute("userList", userService.getAllUsers());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("formTab", "active");
        model.addAttribute("editMode", true);
        model.addAttribute("passwordForm", new ChangePasswordForm(user.getId()));
        return "user-form/user-view";

    }

    @PostMapping("/editUser")
    public String postEditUserForm(@Valid @ModelAttribute("userForm") User user, BindingResult result, ModelMap model) {
        if (result.hasErrors()) {
            model.addAttribute("userForm", user);
            model.addAttribute("formTab", "active");
            model.addAttribute("editMode", true);
            model.addAttribute("passwordForm", new ChangePasswordForm(user.getId()));
        } else {
            try {
                userService.updateUser(user);
                model.addAttribute("userForm", new User());
                model.addAttribute("listTab", "active");

            } catch (Exception e) {
                model.addAttribute("formErrorMessage", e.getMessage());
                model.addAttribute("userForm", new User());
                model.addAttribute("formTab", "active");
                model.addAttribute("roles", roleRepository.findAll());
                model.addAttribute("userList", userService.getAllUsers());
                model.addAttribute("editMode", "true");
                model.addAttribute("passwordForm", new ChangePasswordForm(user.getId()));
            }

            model.addAttribute("userList", userService.getAllUsers());
            model.addAttribute("roles", roleRepository.findAll());
        }
        return "user-form/user-view";
    }

    @GetMapping("/userForm/cancel")
    public String cancelEditUser(ModelMap model) {
        return "redirect:/userForm";
    }


    @GetMapping("/deleteUser/{id}")
    public String deleteUser(Model model, @PathVariable(name = "id") Long id) {
        try {
            userService.deleteUser(id);
        } catch (Exception e) {
            model.addAttribute("deleteError", "The user could not be deleted.");
        }
        return getUserForm(model);
    }
  @PostMapping("/editUser/changePassword")
    public ResponseEntity postEditUserConfirmPassword(@Valid @RequestBody ChangePasswordForm form, Errors errors) {

        try {
            if (errors.hasErrors()) {
                String result = errors.getAllErrors()
                             .stream().map(x->x.getDefaultMessage())
                             .collect(Collectors.joining(""));
                throw new Exception(result);
            }
              userService.ChangePassword(form);
        } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
        }
      return ResponseEntity.ok("success");
    }
}



