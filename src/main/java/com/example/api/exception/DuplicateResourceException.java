package com.example.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * بيتم رميه لما حد يحاول ينشئ مورد لازم يكون فريد (unique) وهو موجود بالفعل.
 * مثال: سجل Inventory لمنتج له سجل بالفعل (علاقة 1:1).
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
