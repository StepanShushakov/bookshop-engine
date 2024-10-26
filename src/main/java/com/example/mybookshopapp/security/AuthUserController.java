package com.example.mybookshopapp.security;

import com.example.mybookshopapp.controllers.FatherController;
import com.example.mybookshopapp.data.dto.cookie.BookIds;
import com.example.mybookshopapp.data.services.BookService;
import com.example.mybookshopapp.data.services.TransactionsService;
import com.example.mybookshopapp.data.services.convertors.Convertor;
import com.example.mybookshopapp.security.model.SmsCode;
import com.example.mybookshopapp.security.services.SmsService;
import com.example.mybookshopapp.data.services.supportingservice.CookieHandlerService;
import com.example.mybookshopapp.errs.UserExistsException;
import com.example.mybookshopapp.security.jwt.JWTBlacklistService;
import com.example.mybookshopapp.security.services.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author karl
 */

@Controller
public class AuthUserController extends FatherController {

    private final SmsService smsService;
    private final JavaMailSender javaMailSender;

    @Autowired
    public AuthUserController(BookstoreUserRegister userRegister,
                              CookieHandlerService cookieHandler,
                              JWTBlacklistService blacklistService,
                              Convertor convertorService,
                              BookService bookService,
                              TransactionsService transactionsService,
                              SmsService smsService,
                              JavaMailSender javaMailSender) {
        super(userRegister, cookieHandler, blacklistService, convertorService, bookService, transactionsService);
        this.smsService = smsService;
        this.javaMailSender = javaMailSender;
    }

    @GetMapping("/signin")
    public String signin(@CookieValue(value = "cartContents", required = false) String cartContents,
                         @CookieValue(value = "postponedContents", required = false) String postponedContents,
                         Model model) {
        getBookIds(cartContents, postponedContents, model);
        return "signin";
    }

    @GetMapping("/signup")
    public String handeSignUp(@CookieValue(value = "cartContents", required = false) String cartContents,
                              @CookieValue(value = "postponedContents", required = false) String postponedContents,
                              Model model) {
        getBookIds(cartContents, postponedContents, model);
        model.addAttribute("regForm", new RegistrationForm());
        return "signup";
    }

    @PostMapping("/requestContactConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestContactConfirmation(@RequestBody ContactConfirmationPayload payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");

        if (!payload.getContact().contains("@")) {
            String smsCodeString = smsService.sendSecretCodeSms(payload.getContact());
            smsService.saveNewCode(new SmsCode(smsCodeString, 60)); //expires in 1 min.
        }
        return response;
    }

    @PostMapping("/requestEmailConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestEmailConfirmation(@RequestBody ContactConfirmationPayload payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("book-shop-app@mail.ru");
        message.setTo(payload.getContact());
        SmsCode smsCode = new SmsCode(smsService.generateCode(), 300);
        smsService.saveNewCode(smsCode);
        message.setSubject("Book store email verification");
        message.setText("Verification code is: " + smsCode.getCode());
        javaMailSender.send(message);
        response.setResult("true");
        return response;
    }

    @PostMapping("/approveContact")
    @ResponseBody
    public ContactConfirmationResponse handleApproveContact(@RequestBody ContactConfirmationPayload payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();

        if(smsService.verifyCode(payload.getCode())){
            response.setResult("true");
        }

        return response;
    }

    @PostMapping("/reg")
    public String handleUserRegistration(RegistrationForm registrationForm,
                                         Model model,
                                         @CookieValue(value = "cartContents", required = false) String cartContents,
                                         @CookieValue(value = "postponedContents", required = false) String postponedContents) throws UserExistsException {
        BookIds slugs = getBookIds(cartContents, postponedContents, model);
        super.getUserRegister().registerNewUser(registrationForm, slugs);
        model.addAttribute("regOk", true);
        return "signin";
    }

    @PostMapping("/login")
    @ResponseBody
    public ContactConfirmationResponse handleLogin(@RequestBody ContactConfirmationPayload payload,
                                                   HttpServletResponse httpServletResponse) {
        ContactConfirmationResponse loginResponse = super.getUserRegister().jwtLogin(payload);
        Cookie cookie = new Cookie("token", loginResponse.getResult());
        httpServletResponse.addCookie(cookie);
        return loginResponse;
    }

    @PostMapping("/login-by-phone-number")
    @ResponseBody
    public ContactConfirmationResponse handleLoginByPhoneNumber(@RequestBody ContactConfirmationPayload payload,
                                                                HttpServletResponse httpServletResponse,
                                                                @CookieValue(value = "cartContents", required = false) String cartContents,
                                                                @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                                                Model model) throws UserExistsException {
        if(smsService.verifyCode(payload.getCode())) {
            BookIds slugs = getBookIds(cartContents, postponedContents, model);
            ContactConfirmationResponse loginResponse = super.getUserRegister().jwtLoginByPhoneNumber(payload, slugs);
            Cookie cookie = new Cookie("token", loginResponse.getResult());
            httpServletResponse.addCookie(cookie);
            return loginResponse;
        }else {
            return null;
        }
    }

//    @GetMapping("/logout")
//    public String handleLogout(HttpServletRequest request) {
//        HttpSession session = request.getSession();
//        SecurityContextHolder.clearContext();
//        if (session != null) {
//            session.invalidate();
//        }
//
//        for (Cookie cookie : request.getCookies()) {
//            cookie.setMaxAge(0);
//        }
//
//        return "redirect:/";
//    }
}
