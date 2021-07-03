-- ----------------------------
-- Table structure for `fs_user`
-- ----------------------------
DROP TABLE IF EXISTS `fs_user`;
CREATE TABLE `fs_user` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'User Id',
    `name` varchar(255) NOT NULL COMMENT 'Nickname',
    `password` varchar(32) NOT NULL COMMENT 'md5(md5(password + static_salt) + salt)',
    `salt` varchar(20) DEFAULT NULL,
    `avatar` varchar(128) DEFAULT NULL COMMENT '头像',
    `register_date` datetime DEFAULT NULL,
    `last_login_date` datetime DEFAULT NULL,
    `login_count` int(11) DEFAULT '0' COMMENT '登录次数',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18912341258 DEFAULT CHARSET=utf8