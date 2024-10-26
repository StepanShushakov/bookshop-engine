package com.example.mybookshopapp.controllers;

import com.example.mybookshopapp.data.ApiResponse;
import com.example.mybookshopapp.data.ApiTypicalResponse;
import com.example.mybookshopapp.data.dto.requests.PaymentRequestDto;
import com.example.mybookshopapp.data.services.BookService;
import com.example.mybookshopapp.data.services.InvoiceService;
import com.example.mybookshopapp.data.services.PaymentService;
import com.example.mybookshopapp.data.services.TransactionsService;
import com.example.mybookshopapp.data.services.convertors.Convertor;
import com.example.mybookshopapp.data.services.supportingservice.CookieHandlerService;
import com.example.mybookshopapp.security.jwt.JWTBlacklistService;
import com.example.mybookshopapp.security.services.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.logging.Logger;

/**
 * @author karl
 */

@Controller
public class PaymentController extends FatherController {

    private final PaymentService paymentService;
    private final InvoiceService invoiceService;
    @Value("${pass.first.test}")
    private String firstTestPass;

    @Autowired
    public PaymentController(BookstoreUserRegister userRegister,
                             CookieHandlerService cookieHandler,
                             JWTBlacklistService blacklistService,
                             Convertor convertorService,
                             BookService bookService,
                             TransactionsService transactionsService,
                             PaymentService paymentService,
                             InvoiceService invoiceService) {
        super(userRegister, cookieHandler, blacklistService, convertorService, bookService, transactionsService);
        this.paymentService = paymentService;
        this.invoiceService = invoiceService;
    }



    @PostMapping("/payment")
    public ResponseEntity<ApiResponse> handlePayment(@RequestBody PaymentRequestDto requestDto) throws NoSuchAlgorithmException {
        Long time = requestDto.getTime();
        String paymentUrl = paymentService.getPaymentUrl(requestDto.getHash(),
                                                            requestDto.getSum(),
                                                            LocalDateTime.ofEpochSecond(time,
                                                                    (int) ((time - (time / 1000) * 1000) * 1000000),
                                                                    ZoneId.systemDefault().getRules().getOffset(Instant.now())),
                                                            "Пополнение счета");
        ApiResponse response = new ApiResponse();
        response.setMessage(paymentUrl);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/paymentResult")
    public ResponseEntity<ApiTypicalResponse> handlePaymentResult(@RequestBody String s) {
        Logger.getLogger(this.getClass().getName()).info("handle payment result action,\npayment result: " + s);
        return new ResponseEntity<>(new ApiTypicalResponse(), HttpStatus.OK);
    }

    @GetMapping("/paymentSuccess")
    public String handlePayment(@RequestParam(value = "SignatureValue") String signatureValue,
                                @RequestParam(value = "OutSum") Double outSum,
                                @RequestParam(value = "InvId") String invId,
                                @RequestParam(value = "Shp_hash") String userHash) throws NoSuchAlgorithmException {
        if (signatureValue.toUpperCase().equals(calculateSuccessUrlSignatureValue(outSum, invId, userHash))) {
            paymentService.putBalance(userHash, outSum);
            invoiceService.buyAllInvoice(invId);
            return "redirect:/profile?paymentSuccess=true#topup";
        } else {
            return "redirect:/profile?paymentSuccess=false#topup";
        }
    }

    private String calculateSuccessUrlSignatureValue(double sum, String invId, String userHash) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update((String.format("%.2f", sum).replace(',','.')+":"+invId+":"+firstTestPass+":Shp_hash="+userHash).getBytes());
        return DatatypeConverter.printHexBinary(md.digest()).toUpperCase();
    }
}
