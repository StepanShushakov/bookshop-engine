package com.example.mybookshopapp.controllers;

import com.example.mybookshopapp.data.dto.requests.SaveAuthorDto;
import com.example.mybookshopapp.data.dto.requests.SaveBookDto;
import com.example.mybookshopapp.data.dto.requests.SaveUserDto;
import com.example.mybookshopapp.data.model.author.AuthorEntity;
import com.example.mybookshopapp.data.model.book.BookEntity;
import com.example.mybookshopapp.data.model.user.UserEntity;
import com.example.mybookshopapp.data.services.AuthorService;
import com.example.mybookshopapp.data.services.BookService;
import com.example.mybookshopapp.data.services.ResourceStorage;
import com.example.mybookshopapp.data.services.TransactionsService;
import com.example.mybookshopapp.data.services.convertors.Convertor;
import com.example.mybookshopapp.data.services.supportingservice.CookieHandlerService;
import com.example.mybookshopapp.errs.BookstoreApiWrongParameterException;
import com.example.mybookshopapp.errs.ParseDateException;
import com.example.mybookshopapp.errs.UserNotPermissionException;
import com.example.mybookshopapp.security.jwt.JWTBlacklistService;
import com.example.mybookshopapp.security.services.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * @author karl
 */

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
public class AdminController extends FatherController{

    private final ResourceStorage storage;
    private final AuthorService authorService;

    @Autowired
    public AdminController(BookstoreUserRegister userRegister,
                           CookieHandlerService cookieHandler,
                           JWTBlacklistService blacklistService,
                           Convertor convertorService,
                           BookService bookService,
                           TransactionsService transactionsService,
                           ResourceStorage storage,
                           AuthorService authorService) {
        super(userRegister, cookieHandler, blacklistService, convertorService, bookService, transactionsService);
        this.storage = storage;
        this.authorService = authorService;
    }

    @GetMapping("")
    public String handleAdminPage() {
        return "redirect:/admin/book";
    }

    @GetMapping("/book")
    public String handleAdminBookPage() {
        return "/admin/book/index";
    }

    @GetMapping("/author")
    public String handleAdminAuthorPage() {
        return "/admin/author/index";
    }

    @GetMapping("/user")
    public String handleAdminUserPage() {
        return  "/admin/user/index";
    }

    @GetMapping("/book/find")
    public String findBook(@RequestParam("slug") String slug, RedirectAttributes redirectAttributes) throws BookstoreApiWrongParameterException {
        List<BookEntity> bookEntities = new ArrayList<>();
        BookEntity bookEntity = getBookService().getBookEntityBySlug(slug);
        if (bookEntity == null) {
            bookEntities = getBookService().getBookEntitiesByTitle(slug);
        } else {
            bookEntities.add(bookEntity);
        }
        List<SaveBookDto> saveBookDtos = new ArrayList<>();
        bookEntities.forEach(e -> saveBookDtos.add(new SaveBookDto(e)));
        redirectAttributes.addFlashAttribute("books", saveBookDtos);
        return "redirect:/admin/book";
    }

    @GetMapping("/book/delete")
    public String deleteBook(@RequestParam("id") int id) {
        getBookService().deleteBookById(id);
        return "redirect:/admin/book";
    }

    @GetMapping("/book/edit")
    public String editBook(@RequestParam(value = "id", required = false) Integer id,
                           Model model) {
        if (id == null) {
            model.addAttribute("bookDto", new SaveBookDto());
        } else {
            model.addAttribute("bookDto", getConvertorService().convertBookEntityToSaveDto(
                    getBookService().getBookEntityById(id)));
        }
        return "admin/book/slug";
    }

    @PostMapping("/book/{slug}/img/save")
    public String saveNewBookImage(@RequestParam("file") MultipartFile file,
                                   @PathVariable("slug") String slug,
                                   @CookieValue(value = "token", required = false) String token) throws UserNotPermissionException {
        getUserRegister().checkAuthorization(token);
        String savePath;
        savePath = storage.saveNewImage(file, slug);
        BookEntity bookEntityToUpdate = getBookService().getBookEntityBySlug(slug);
        bookEntityToUpdate.setImage(savePath);
        getBookService().save(bookEntityToUpdate);   //save new path in db here
        return "redirect:/admin/book/edit?id=" + bookEntityToUpdate.getId();
    }

    @PostMapping("/saveBook")
    public String saveBook(@ModelAttribute SaveBookDto saveBookDto) throws ParseDateException {
        return "redirect:/admin/book/edit?id=" + getBookService().save(saveBookDto);
    }

    @GetMapping("/author/find")
    public String findAuthor(@RequestParam("slug") String slug, RedirectAttributes redirectAttributes) {
        List<AuthorEntity> authorEntities = new ArrayList<>();
        AuthorEntity authorEntity = authorService.getAuthorEntityBySlug(slug);
        if (authorEntity == null) {
            authorEntities = authorService.getAuthorEntitiesByName(slug);
        } else {
            authorEntities.add(authorEntity);
        }
        List<SaveAuthorDto> saveAuthorDtos = new ArrayList<>();
        authorEntities.forEach(e -> saveAuthorDtos.add(new SaveAuthorDto(e)));
        redirectAttributes.addFlashAttribute("authors", saveAuthorDtos);
        return "redirect:/admin/author";
    }

    @GetMapping("/author/edit")
    public String editAuthor(@RequestParam(value = "id", required = false) Integer id,
                           Model model) {
        if (id == null) {
            model.addAttribute("authorDto", new SaveAuthorDto());
        } else {
            model.addAttribute("authorDto", new SaveAuthorDto(authorService.getAuthorEntityById(id)));
        }
        return "admin/author/slug";
    }

    @PostMapping("/author/{slug}/img/save")
    public String saveNewAuthorPhoto(@RequestParam("file") MultipartFile file,
                                   @PathVariable("slug") String slug,
                                   @CookieValue(value = "token", required = false) String token) throws UserNotPermissionException {
        getUserRegister().checkAuthorization(token);
        String savePath;
        savePath = storage.saveNewImage(file, slug);
        AuthorEntity authorEntityToUpdate = authorService.getAuthorEntityBySlug(slug);
        authorEntityToUpdate.setPhoto(savePath);
        authorService.save(authorEntityToUpdate);   //save new path in db here
        return "redirect:/admin/author/edit?id=" + authorEntityToUpdate.getId();
    }

    @PostMapping("/saveAuthor")
    public String saveAuthor(@ModelAttribute SaveAuthorDto saveAuthorDto) {
        authorService.save(saveAuthorDto);
        return "redirect:/admin/author/edit?id=" + saveAuthorDto.getId();
    }

    @GetMapping("/user/find")
    public String findUser(@RequestParam("slug") String slug, RedirectAttributes redirectAttributes) {
        List<UserEntity > userEntities = new ArrayList<>();
        UserEntity userEntity = getUserRegister().getUserEntityByPhone(slug);
        if (userEntity == null) {
            userEntities = getUserRegister().getUserEntitiesOnEmail(slug);
        } else {
            userEntities.add(userEntity);
        }
        List<SaveUserDto> saveUserDtos = new ArrayList<>();
        userEntities.forEach(e -> saveUserDtos.add(new SaveUserDto(e)));
        redirectAttributes.addFlashAttribute("users", saveUserDtos);
        return "redirect:/admin/user";
    }

    @GetMapping("/user/change")
    public String changeUser(@RequestParam(value = "id") Integer id, Model model) {
        model.addAttribute("userDto", new SaveUserDto(getUserRegister().getUserEntityById(id)));
        return "admin/user/slug";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute SaveUserDto saveUserDto) {
        getUserRegister().save(saveUserDto);
        return "redirect:/admin/user/change?id=" + saveUserDto.getId();
    }
}
