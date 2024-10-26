package com.example.mybookshopapp.data.services;

import com.example.mybookshopapp.data.dto.BookDto;
import com.example.mybookshopapp.data.dto.UserDto;
import com.example.mybookshopapp.data.model.enums.InvoiceState;
import com.example.mybookshopapp.data.model.payments.BalanceTransactionEntity;
import com.example.mybookshopapp.data.model.payments.InvoiceEntity;
import com.example.mybookshopapp.data.model.user.UserEntity;
import com.example.mybookshopapp.data.repositories.BalanceTransactionRepository;
import com.example.mybookshopapp.data.repositories.InvoiceRepository;
import com.example.mybookshopapp.security.repositories.BookstoreUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author karl
 */

@Service
public class PaymentService {

    private final BookstoreUserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final BalanceTransactionRepository balanceTransactionRepository;

    @Value("${robokassa.merchant.login}")
    private String merchantLogin;

    @Value("${pass.first.test}")
    private String firstTestPass;

    public PaymentService(BookstoreUserRepository userRepository, InvoiceRepository invoiceRepository, BalanceTransactionRepository balanceTransactionRepository) {
        this.userRepository = userRepository;
        this.invoiceRepository = invoiceRepository;
        this.balanceTransactionRepository = balanceTransactionRepository;
    }

    public String getPaymentUrl(UserDto userDto,  String cartContents, List<BookDto> booksFromCookieSlugs) throws NoSuchAlgorithmException {
        double paymentSumTotal = booksFromCookieSlugs.stream().mapToDouble(BookDto::getDiscountPrice).sum();
        InvoiceEntity invoice = createInvoice(userRepository.findUserEntityById(userDto.getId()),
                LocalDateTime.now(),
                cartContents,
                (int) paymentSumTotal);
        return getPaymentUrlOnSum(userDto.getHash(),
                                    paymentSumTotal,
                                    String.valueOf(invoice.getId())
        );
    }

    private InvoiceEntity createInvoice(UserEntity userEntity, LocalDateTime time, String contents, Integer sum) {
        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setUser(userEntity);
        invoice.setTime(time);
        invoice.setContents(contents);
        invoice.setSum(sum);
        invoice.setState(InvoiceState.NEW);
        invoiceRepository.save(invoice);
        return invoice;
    }

    public String getPaymentUrl(String userHash, Integer sum, LocalDateTime time, String content) throws NoSuchAlgorithmException {
        return getPaymentUrlOnSum(userHash,
                                    (double) sum,
                                    String.valueOf(createInvoice(userRepository.findUserEntityByHash(userHash),
                                                                    time,
                                                                    content,
                                                                    sum).getId())
        );
    }

    private String getPaymentUrlOnSum(String userHash, double sum, String invId) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update((merchantLogin+":"+sum+":"+invId+":"+firstTestPass+":Shp_hash="+userHash).getBytes());
        return "https://auth.robokassa.ru/Merchant/Index.aspx" +
                "?MerchantLogin=" + merchantLogin +
                "&InvId=" + invId +
                "&Culture=ru" +
                "&Encoding=utf-8" +
                "&OutSum=" + sum +
                "&SignatureValue=" + DatatypeConverter.printHexBinary(md.digest()).toUpperCase() +
                "&IsTest=1" +
                "&Shp_hash=" + userHash;
    }

    public void putBalance(String userHash, Double sum) {
        BalanceTransactionEntity balanceTransaction = new BalanceTransactionEntity();
        balanceTransaction.setUser(userRepository.findUserEntityByHash(userHash));
        balanceTransaction.setValue((int) Math.abs(sum));
        balanceTransaction.setDescription("Пополнение баланса");
        balanceTransaction.setTime(LocalDateTime.now());
        balanceTransactionRepository.save(balanceTransaction);
    }
}
