package com.example.textilemarketplacebackend.mail.controllers;

import com.example.textilemarketplacebackend.global.services.ResponseHandlerService;
import com.example.textilemarketplacebackend.mail.models.InternalMailServiceErrorException;
import com.example.textilemarketplacebackend.mail.models.InvalidMailRequestException;
import com.example.textilemarketplacebackend.mail.models.MailRequest;
import com.example.textilemarketplacebackend.mail.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class MailController {

    private final EmailService emailService;
    private final ResponseHandlerService responseService;

    @PostMapping("/send_notif")
    public ResponseEntity<Object> sendNotificationEmail(@RequestBody List<MailRequest> mailRequestList) {
        try {
            return responseService.generateResponse("Response and status for emails has been fetched", HttpStatus.CREATED, emailService.sendEmail(mailRequestList));
        } catch (InvalidMailRequestException e) {
            return responseService.generateResponse("Incorrect parameters provided to the api.", HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (InternalMailServiceErrorException e) {
            return responseService.generateResponse("Incorrect parameters provided to the api.", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (IllegalStateException e) {
            return responseService.generateResponse("A problem occurred involving the api response.", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            return responseService.generateResponse("Internal error occurred.", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
