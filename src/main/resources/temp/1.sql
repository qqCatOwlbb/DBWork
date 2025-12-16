/*
SQLyog Community v13.2.1 (64 bit)
MySQL - 8.0.40 : Database - xitu
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`xitu` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `xitu`;

/*Table structure for table `article_likes` */

DROP TABLE IF EXISTS `article_likes`;

CREATE TABLE `article_likes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `article_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `idx_like_article` (`article_id`),
  CONSTRAINT `article_likes_ibfk_1` FOREIGN KEY (`article_id`) REFERENCES `articles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `article_likes_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb3;

/*Data for the table `article_likes` */

insert  into `article_likes`(`id`,`article_id`,`user_id`,`created_at`) values 
(8,1,1,'2025-02-18 20:19:04'),
(9,2,1,'2025-02-18 20:19:10'),
(10,3,1,'2025-02-18 20:19:13'),
(11,1,1,'2025-02-18 21:18:29'),
(12,1,12,'2025-03-07 09:49:17'),
(13,2,12,'2025-03-07 09:49:17'),
(23,9,13,'2025-04-15 19:50:55'),
(24,8,13,'2025-04-15 19:50:55');

/*Table structure for table `articles` */

DROP TABLE IF EXISTS `articles`;

CREATE TABLE `articles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `content` text NOT NULL,
  `author_id` bigint NOT NULL,
  `view_count` int DEFAULT '0',
  `like_count` int DEFAULT '0',
  `status` tinyint DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_article_author` (`author_id`),
  CONSTRAINT `articles_ibfk_1` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3;

/*Data for the table `articles` */

insert  into `articles`(`id`,`title`,`content`,`author_id`,`view_count`,`like_count`,`status`,`created_at`,`updated_at`) values 
(1,'第一篇文章','这是第一篇文章的内容，测试数据库插入。',1,13,1,1,'2025-02-17 16:32:52','2025-03-07 09:49:17'),
(2,'第二篇文章','这是第二篇文章的内容，包含更多测试数据。',1,20,1,1,'2025-02-17 16:32:52','2025-03-07 09:49:17'),
(3,'测试标题1','测试文本1',1,0,1,1,'2025-02-18 07:29:14','2025-02-18 20:19:13'),
(5,'测试标题','测试内容',1,0,0,1,'2025-03-04 21:23:36','2025-03-04 21:23:36'),
(8,'身体是革命的本钱','一日三餐不能少',13,9,1,1,'2025-04-15 19:34:05','2025-04-17 10:14:31'),
(9,'身体是革命的本钱','一日三餐不能少2',13,0,1,1,'2025-04-15 19:34:12','2025-04-15 19:46:55'),
(10,'身体是革命的本钱','一日三餐不能少3',13,0,0,1,'2025-04-15 19:34:17','2025-04-15 19:34:17');

/*Table structure for table `comments` */

DROP TABLE IF EXISTS `comments`;

CREATE TABLE `comments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `article_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `content` text NOT NULL,
  `parent_comment_id` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `parent_comment_id` (`parent_comment_id`),
  KEY `idx_comment_article` (`article_id`),
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`article_id`) REFERENCES `articles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `comments_ibfk_3` FOREIGN KEY (`parent_comment_id`) REFERENCES `comments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb3;

/*Data for the table `comments` */

insert  into `comments`(`id`,`article_id`,`user_id`,`content`,`parent_comment_id`,`created_at`) values 
(7,1,1,'这是我的第一条评论',NULL,'2025-02-18 16:14:53'),
(9,1,1,'这是我的第二条评论',7,'2025-02-18 16:17:15'),
(10,1,1,'第三条评论',7,'2025-02-18 21:33:48'),
(11,1,5,'测试跨域问题',NULL,'2025-02-21 14:33:50'),
(12,1,5,'测试跨域问题1',NULL,'2025-02-21 14:34:53'),
(13,1,5,'测试跨域问题1',NULL,'2025-02-21 14:37:51'),
(14,1,5,'测试跨域问题1',NULL,'2025-02-21 14:50:18'),
(15,1,5,'测试跨域问题1',NULL,'2025-02-21 15:18:06'),
(16,1,5,'测试跨域问题1',NULL,'2025-02-21 15:19:55'),
(17,1,5,'测试跨域问题1',11,'2025-02-21 16:10:00'),
(18,1,5,'测试跨域问题1',11,'2025-02-21 16:10:28');

/*Table structure for table `users` */

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `bio` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb3;

/*Data for the table `users` */

insert  into `users`(`id`,`username`,`password`,`avatar`,`bio`,`created_at`,`updated_at`) values 
(1,'test_user','test_password','avatar_url','This is a test user.','2025-02-17 16:32:06','2025-02-17 16:32:06'),
(2,'test_user1','123456','avatar_url','这个人很沉默','2025-02-18 22:40:30','2025-02-18 22:40:30'),
(3,'test_user2','123456','avatar_url','这个人很沉默\r\n','2025-02-18 22:41:10','2025-02-18 22:41:10'),
(4,'test_user3','123456','avatar_url','这个人很沉默','2025-02-18 22:41:40','2025-02-18 22:41:55'),
(5,'张三1','$2a$10$XIlFgTloN7toQvIlVH.LDeEErb.RFfjOHxEWJE9GPGYCQ.7AsATxe','a','d','2025-02-19 23:15:01','2025-02-21 15:37:50'),
(6,'李四1','$2a$10$9EICxw/YNxFZUN0KSYujnOqEfBZFjZF7tC.ShWc.dd9m2UDZWZk.a','a',NULL,'2025-02-20 08:59:05','2025-02-20 12:57:34'),
(7,'王五','$2a$10$JcxDVkkP8mlxUKF1ATzR7.D3Cm1cq39rGVUSNl1qcIvFwm2uX0yEm',NULL,NULL,'2025-02-20 10:20:10','2025-02-20 10:20:10'),
(8,'赵六','$2a$10$SuHZ0Rn9t1vbf8tx9dsfm.OI1RrCHoD5jTzFvDIJLt2db9bKsuAC.',NULL,NULL,'2025-02-20 12:41:11','2025-02-20 12:41:11'),
(9,'张三','$2a$10$8aOXtgK/qOFxhLf9JuGnbeif9CEtV.6vDl55rwozS3khCQ.MK9EBa',NULL,NULL,'2025-02-20 12:41:57','2025-02-20 12:41:57'),
(10,'草泥马','$2a$10$/ppglTiNIFYBTiSu5EViDOtMxCvg/NgBj6UnKl5xx9vFueQIS75WC',NULL,NULL,'2025-02-20 21:58:06','2025-02-20 21:58:06'),
(12,'我是老八','$2a$10$gt18K.bhYXJ5V0tlE975ZOej3lrDgy95wHYCodNBLqgcoX9S0V2Be','/uploads/avatar-ee115a9c-989f-4417-a916-2b624163b351.jpg','干了兄弟们','2025-03-05 19:13:30','2025-03-07 12:43:47'),
(13,'老八','$2a$10$LI09XPQUk6ua.69mO2f0tuznT/dYsT.mM3e8HSpR9RqKn.4dRa6PW',NULL,NULL,'2025-03-05 20:20:19','2025-03-05 20:20:19');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
