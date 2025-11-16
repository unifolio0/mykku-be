-- Remove unique constraint from fcm_token.token column to allow token reassignment
-- FCM tokens can be reassigned to different users (e.g., app reinstall)

ALTER TABLE fcm_token DROP INDEX token;
