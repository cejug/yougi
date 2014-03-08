--liquibase formatted sql

--changeset htmfilho:2
insert into application_property values ('timezone', 'UTC 0:00'),
                                        ('url', 'http://localhost:8080/ug'),
                                        ('sendEmails', 'false'),
                                        ('groupName', 'Yougi UG'),
                                        ('language', 'en'),
                                        ('emailServerType', 'pop3'),
                                        ('captchaEnabled', 'false'),
                                        ('captchaPrivateKey', ''),
                                        ('captchaPublicKey', ''),
                                        ('fileRepositoryPath', '');

insert into message_template (id, title, body) values
    ('03BD6F3ACE4C48BD8660411FC8673DB4', '[UG] Registration Deactivated', '<p>Dear <b>#{userAccount.firstName}</b>,</p><p>We are very sorry to inform that we cannot keep you as a UG member.</p><p>Reason: <i>#{userAccount.deactivationReason}</i></p><p>We kindly appologize for the inconvenience and we count on your understanding.</p><p>Best Regards,</p><p><b>UG Leadership Team</b></p>'),
    ('0D6F96382D91454F8155A720F3326F1B', '[UG Admin] A New Member Joint the Group', '<p>Dear UG Leader,</p><p><b>#{userAccount.fullName}</b> joint the UG at #{userAccount.registrationDate}.</p><p>Regards,</p><p><b>UG Management</b></p>'),
    ('47DEE5C2E0E14F8BA4605F3126FBFAF4', '[UG] Welcome to UG', '<p>Hi <b>#{userAccount.firstName}</b>,</p><p>you are confirmed as a member of the UG. Welcome to the <b><a href=''http://www.cejug.org''>UG Community</a></b>!</p><p>Thank you!</p><p><b>UG Leadership Team</b></p>'),
    ('67BE6BEBE45945D29109A8D6CD878344', '[UG] Request for Password Change', '<p>Hi <b>#{userAccount.firstName}</b>,</p><p>you requested to change your password. The authorization code to perform this operation is:</p><p>#{userAccount.confirmationCode}</p><p>Inform this code in the form that you saw right after requesting the new password or just follow the link below to fill out the form automatically:</p><p><a href=''http://#{serverAddress}/change_password.xhtml?cc=#{userAccount.confirmationCode}''>http://#{serverAddress}/change_password.xhtml?cc=#{userAccount.confirmationCode}</a></p><p>Thank you!<br/>\r\n\r\n<b>UG Leadership Team</b></p>'),
    ('KJZISKQBE45945D29109A8D6C92IZJ89', '[UG] Request for Email Change', '<p>Hi <b>#{userAccount.firstName}</b>,</p><p>you requested to change your email address from <i>#{userAccount.email}</i> to <i>#{userAccount.unverifiedEmail}</i>. The authorization code to perform this operation is:</p><p>#{userAccount.confirmationCode}</p><p>Inform this code in the form that you saw right after changing the email address or just follow the link below:</p><p><a href=''http://#{serverAddress}/change_email_confirmation.xhtml?cc=#{userAccount.confirmationCode}''>http://#{serverAddress}/change_email_confirmation.xhtml?cc=#{userAccount.confirmationCode}</a></p><p>Thank you!<br/>\r\n\r\n<b>UG Leadership Team</b></p>'),
    ('E3F122DCC87D42248872878412B34CEE', '[UG] Registration Confirmation', '<p>Hi <b>#{userAccount.firstName}</b>,</p><p>you seems to register yourself as a member of UG. We would like to confirm your email address to be able to contact you when necessary. You just have to click on the link below to confirm your email:</p><p><a href=''http://#{serverAddress}/EmailConfirmation?code=#{userAccount.confirmationCode}''>http://#{serverAddress}/EmailConfirmation?code=#{userAccount.confirmationCode}</a></p><p>If the address above does not look like a link, please select, copy and paste it your web browser. If you do not registered on UG and beleave that this message was sent by mistake, please ignore it and accept our apologes.</p><p>Best Regards,</p><p><b>UG Leadership Team</b></p>'),
    ('IKWMAJSNDOE3F122DCC87D4224887287', '[UG] Membership Deactivated', '<p>Hi <b>#{userAccount.firstName}</b>,</p><p>we just knew that you wanna leave us :( Thank you for all contributions you have made to the UG community.</p><p>All the best,</p><p><b>UG Leadership Team</b></p>'),
    ('0D6F96382IKEJSUIWOK5A720F3326F1B', '[UG Admin] A Member Was Deactivated', '<p>Dear UG Leader,</p><p><b>#{userAccount.fullName}</b> was deactivated from the UG due to the following reason:</p><p><i>#{userAccount.deactivationReason}</i></p><p>Regards,</p><p><b>UG Management</b></p>'),
    ('09JDIIE82O39IDIDOSJCHXUDJJXHCKP0', '[UG Admin] Group Assigment', '<p>Hi <b>#{userAccount.firstName}</b>,</p><p>You were assigned to the <b>#{accessGroup.name}</b> group. Changes on your rights may apply.</p><p>Regards,</p><p><b>UG Management</b></p> '),
    ('KJDIEJKHFHSDJDUWJHAJSNFNFJHDJSLE', '[UG] Event Attendance', '<p>Hi <b>#{userAccount.firstName}</b>,</p><p>you have confirmed your attendance in the event <b>#{event.name}</b> that will take place at <b>#{event.venue}</b>, on <b>#{event.startDate}</b>, from <b>#{event.startTime}</b> to <b>#{event.endTime}</b>.</p><p>We are looking forward to see you there!</p><p>Best Regards,</p><p><b>UG Leadership Team</b></p>');

insert into language values ('en', 'English');
insert into language values ('pt', 'Portugues');

insert into access_group (id, name, description, user_default) values
    ('PQOWKSIFUSLEOSJFNMDKELSOEJDKNWJE', 'helpers', 'Helpers', 0),
    ('IKSJDKMSNDJUEIKWQJSHDNCMXKLOPIKJ', 'partners', 'Partners', 0);
