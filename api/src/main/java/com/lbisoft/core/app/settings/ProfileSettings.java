package com.lbisoft.core.app.settings;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Profile;

public class ProfileSettings {
    @Retention(RUNTIME)
    @Target({ TYPE, METHOD })
    @Profile({ "LOCAL", "DEV" })
    public @interface InDevelopment {
    }

    @Retention(RUNTIME)
    @Target({ TYPE, METHOD })
    @Profile({ "PROD", "QA" })
    public @interface InProductionOrQA {
    }

    @Retention(RUNTIME)
    @Target({ TYPE, METHOD })
    @Profile({ "PROD" })
    public @interface ProductionOnly {
    }

    @Retention(RUNTIME)
    @Target({ TYPE, METHOD })
    @Profile({ "LOCAL" })
    public @interface LocalOnly {
    }

    @Retention(RUNTIME)
    @Target({ TYPE, METHOD })
    @Profile({ "QA" })
    public @interface QAOnly {
    }
}
