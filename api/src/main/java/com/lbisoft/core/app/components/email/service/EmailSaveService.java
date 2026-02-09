package com.lbisoft.core.app.components.email.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.lbisoft.core.app.components.email.model.Email;
import com.lbisoft.core.app.components.email.model.PersonEmail;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.generated.EmailDbMapper;
import com.lbisoft.core.app.db.mapper.generated.PersonEmailDbMapper;
import com.lbisoft.core.app.db.model.generated.EmailDb;
import com.lbisoft.core.app.db.model.generated.PersonEmailDb;
import com.lbisoft.core.app.db.model.generated.PersonEmailDbExample;
import com.lbisoft.core.app.db.model.generated.PersonEmailDbKey;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ProviderBuilder;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class EmailSaveService {
  private final Dao<EmailDb, Integer> dao;
  private final Dao<PersonEmailDb, PersonEmailDbKey> personDao;
  private final AbstractProvider<EmailDb, Email> provider;

  @Autowired
  public EmailSaveService(EmailDbMapper mapper, PersonEmailDbMapper personEmailMapper) {
    dao = Dao.getDao(mapper, EmailDb::getEmailId);
    personDao = Dao.getDao(personEmailMapper, p -> p);
    provider = ProviderBuilder.simple(EmailDb.class, Email.class);
  }

  public void saveEmailsForPerson(Integer personId, List<PersonEmail> emails, AppUser user) {
    PersonEmailDbExample existingEmailExample = new PersonEmailDbExample();
    existingEmailExample.createCriteria().andPersonIdEqualTo(personId);
    List<PersonEmailDb> existingPersonEmails = personDao.select(existingEmailExample);

    if (emails.size() == existingPersonEmails.size()) {
      // check if emails are the same ids in the same order
      boolean same = true;
      for (int i = 0; i < emails.size(); i++) {
        if (!emails.get(i).getEmailId().equals(existingPersonEmails.get(i).getEmailId())) {
          same = false;
          break;
        }
      }
      if (same) {
        // if they are in the same order then just update, don't delete all and insert.
        List<EmailDb> mappedEmailsToUpdate = provider.reverseMapAll(emails);
        dao.update(mappedEmailsToUpdate, user);
        return;
      }
    }

    // Delete all of existing personEmails
    personDao.delete(existingEmailExample);

    // delete all existing emails (actual emails)
    for (PersonEmailDb p : existingPersonEmails) {
      dao.delete(p.getEmailId());
    }

    List<EmailDb> mappedEmails = provider.reverseMapAll(emails);
    dao.insert(mappedEmails, user);

    List<PersonEmailDb> personEmailsToInsert = mappedEmails.stream().map(e -> {
      PersonEmailDb emailDb = new PersonEmailDb();
      emailDb.setEmailId(e.getEmailId());
      emailDb.setPersonId(personId);
      return emailDb;
    }).toList();
    personDao.insert(personEmailsToInsert, user);
  }
}
