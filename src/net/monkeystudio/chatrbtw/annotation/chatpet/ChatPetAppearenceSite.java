package net.monkeystudio.chatrbtw.annotation.chatpet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by bint on 2018/5/9.
 */


@Target({ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ChatPetAppearenceSite {
    int value();
}
