package com.example.mybookshopapp.controllers;

import com.example.mybookshopapp.data.dto.TransactionDto;
import com.example.mybookshopapp.data.dto.requests.ProfileChangeRequestDto;
import com.example.mybookshopapp.data.model.user.ProfileChangeEntity;
import com.example.mybookshopapp.data.model.user.UserEntity;
import com.example.mybookshopapp.data.repositories.ProfileChangeRepository;
import com.example.mybookshopapp.data.services.BookService;
import com.example.mybookshopapp.data.services.TransactionsService;
import com.example.mybookshopapp.data.services.convertors.Convertor;
import com.example.mybookshopapp.data.services.supportingservice.CookieHandlerService;
import com.example.mybookshopapp.errs.EmptyPasswordException;
import com.example.mybookshopapp.errs.NotFoundProfileTokenException;
import com.example.mybookshopapp.errs.PasswordReplyException;
import com.example.mybookshopapp.security.jwt.JWTBlacklistService;
import com.example.mybookshopapp.security.services.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * @author karl
 */

@Controller
@RequestMapping("/profile")
public class ProfileController extends FatherController{

    private final JavaMailSender javaMailSender;
    private final ProfileChangeRepository profileChangeRepository;

    @Autowired
    public ProfileController(BookstoreUserRegister userRegister,
                             CookieHandlerService cookieHandler,
                             JWTBlacklistService blacklistService,
                             Convertor convertorService,
                             TransactionsService transactionsService,
                             BookService bookService,
                             JavaMailSender javaMailSender,
                             ProfileChangeRepository profileChangeRepository) {
        super(userRegister, cookieHandler, blacklistService, convertorService, bookService, transactionsService);
        this.javaMailSender = javaMailSender;
        this.profileChangeRepository = profileChangeRepository;
    }

    @ModelAttribute("transactionList")
    public List<TransactionDto> transactions() {
        return getConvertorService().convertBalanceTransactionEntityToDto(
                getTransactionsService()
                        .getPageOfUserTransactions((UserEntity) getUserRegister().getCurrentUser(), "desc", 0, 2).getContent());
    }

    @GetMapping("")
    public String handleProfile(@RequestParam(value = "paymentSuccess", required = false) Boolean paymentSuccess,
                                @RequestParam(value = "confirmToken", required = false) String confirmToken,
                                Model model,
                                RedirectAttributes redirectAttributes) throws NotFoundProfileTokenException {
        addBooksCountModeleAttributeFromDatabase(getUserRegister().getCurrentUserDto().getId(), model);
        if (paymentSuccess != null) {
            if (paymentSuccess) {
                model.addAttribute("PaymentSuccess", paymentSuccess);
            } else
                model.addAttribute("PaymentFail", true);

        }
        if (confirmToken != null) {
            ProfileChangeEntity profileChangeEntity = profileChangeRepository.findByToken(confirmToken);
            BookstoreUserRegister userRegister = getUserRegister();
            if (profileChangeEntity != null &&
                    ((UserEntity) userRegister.getCurrentUser()).getId() == profileChangeEntity.getUser().getId()) {
                userRegister.updateUserOnRequest(profileChangeEntity);
                redirectAttributes.addFlashAttribute("success", true);
                return "redirect:/profile";
            } else {
                throw new NotFoundProfileTokenException("профиль не обновлен: при переходе по ссылке не удалось получить токен для обновления профиля");
            }
        }
        return "profile";
    }

    @PostMapping("/save")
    public String saveProfile(@ModelAttribute ProfileChangeRequestDto profileChangeRequest, RedirectAttributes attributes) throws EmptyPasswordException, PasswordReplyException {
         if (profileChangeRequest.getPassword().isEmpty()) {
             throw new EmptyPasswordException("необходимо указать пароль");
         }
         if (!profileChangeRequest.getPassword().equals(profileChangeRequest.getPasswordReply())) {
             throw new PasswordReplyException("пароль и подтверждение пароля не совпадают");
         }
         ProfileChangeEntity profileChangeEntity = getUserRegister().createProfileChangeRecord(profileChangeRequest);
         createConfirmMailMessage(profileChangeEntity);
         attributes.addFlashAttribute("confirmMailSend", true);
         return "redirect:/profile#basic";
    }

    private void createConfirmMailMessage(ProfileChangeEntity profileChangeEntity) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("book-shop-app@mail.ru");
        message.setTo(profileChangeEntity.getEmail());
        message.setSubject("Book store profile change confirmation");
        message.setText("To update your profile, follow the confirmation link\n"
                + "http://176.196.0.80/profile?confirmToken=" + profileChangeEntity.getToken());
        javaMailSender.send(message);
    }
}
