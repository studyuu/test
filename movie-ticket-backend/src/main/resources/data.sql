-- 初始化轮播图数据
INSERT INTO banner (image, title, description, movie_id, order_num, status) VALUES
('https://picsum.photos/1920/500?random=100', '流浪地球3', '国产科幻巨制，震撼视觉盛宴', 1, 1, 1),
('https://picsum.photos/1920/500?random=101', '热辣滚烫', '贾玲导演新作，笑中带泪', 2, 2, 1),
('https://picsum.photos/1920/500?random=102', '飞驰人生2', '沈腾范丞丞，笑翻全场', 3, 3, 1);

-- 初始化电影数据
INSERT INTO movie (movie_name, poster, movie_type, region, duration, director, actors, synopsis, release_date, status, rating, english_title, price) VALUES
('流浪地球3', 'https://picsum.photos/300/450?random=1', '科幻/冒险', '中国大陆', 148, '郭帆', '吴京, 刘德华, 李雪健, 沙溢', '太阳即将毁灭，人类在地球表面建造出巨大的推进器，寻找新的家园。然而宇宙之路危机四伏，为了拯救地球，流浪地球时代的年轻人再次挺身而出，展开争分夺秒的生死之战。', '2025-02-10', 1, 9.2, 'The Wandering Earth III', 45),
('热辣滚烫', 'https://picsum.photos/300/450?random=2', '喜剧/剧情', '中国大陆', 129, '贾玲', '贾玲, 雷佳音, 张小斐, 杨紫', '乐莹宅家多年，无所事事。大学毕业工作了一段时间后，她选择脱离社会，封闭社交圈层，这是她认为与自己"和解"的最好方式。一日，在命运的几番"捉弄"下，她决定要换一种方式生活。', '2025-01-29', 1, 8.8, 'YOLO', 42),
('飞驰人生2', 'https://picsum.photos/300/450?random=3', '喜剧/运动', '中国大陆', 121, '韩寒', '沈腾, 范丞丞, 尹正, 张本煜', '昔日冠军车手张驰沦为落魄驾校教练，过着靠脸吃饭勉强度日的生活。不料天上掉馅饼，濒临停产的老头乐车厂厂长主动提出赞助张驰组建车队再闯最后一届巴音布鲁克拉力赛。', '2025-01-29', 1, 8.5, 'Pegasus 2', 40),
('第二十条', 'https://picsum.photos/300/450?random=4', '剧情/喜剧', '中国大陆', 141, '张艺谋', '雷佳音, 马丽, 赵丽颖, 高叶', '这一年的不容易谁能懂？自打挂职到市检察院，韩明的糟心事就接二连三。儿子韩雨辰打了校领导儿子并拒绝道歉，妻子李茂娟义愤填膺继而揍了校领导，补刀成功。', '2025-01-29', 1, 8.9, 'Article 20', 43),
('熊出没·逆转时空', 'https://picsum.photos/300/450?random=5', '动画/喜剧', '中国大陆', 108, '林汇达', '谭笑, 张秉君, 张伟, 刘思奇', '光头强是一名普通程序员，却常梦见陌生的森林和两头狗熊。直到他跟上司出访大客户，终于想起：他原是森林的小导游，偶然得到一次重新选择人生的机会。', '2025-01-29', 1, 8.3, 'Boonie Bears: Back to Earth', 35),
('沙丘2', 'https://picsum.photos/300/450?random=6', '科幻/动作', '美国', 166, '丹尼斯·维伦纽瓦', '提莫西·查拉梅, 赞达亚, 丽贝卡·弗格森', '保罗·厄崔迪公爵加入了弗雷曼人阵营，开始成为他们的精神领袖，并决心对毁灭他家族的阴谋者进行报复。', '2025-03-08', 2, 9.0, 'Dune: Part Two', 48);

-- 初始化影院数据
INSERT INTO cinema (cinema_name, address, phone, description, status, rating, is_deleted, is_hot) VALUES
('万达影城（CBD店）', '北京市朝阳区建国路88号万达广场5层', '010-88886666', '万达影城CBD店位于北京市朝阳区建国路88号万达广场5层，拥有12个放映厅，包括IMAX厅、杜比全景声厅等，是北京市中心的现代化影院。', 1, 4.8, 0, 1),
('CGV影城（颐堤港店）', '北京市朝阳区酒仙桥路18号颐堤港4层', '010-66668888', 'CGV影城颐堤港店位于北京市朝阳区酒仙桥路18号颐堤港4层，拥有8个放映厅，包括IMAX厅、ScreenX厅等，是北京市东部的高端影院。', 1, 4.9, 0, 1),
('博纳国际影城（朝阳门店）', '北京市朝阳区朝阳门外大街18号丰联广场3层', '010-55559999', '博纳国际影城朝阳门店位于北京市朝阳区朝阳门外大街18号丰联广场3层，拥有10个放映厅，包括IMAX厅、中国巨幕厅等，是北京市东部的大型影院。', 1, 4.7, 0, 1),
('金逸影城（大悦城店）', '北京市朝阳区朝阳北路101号朝阳大悦城8层', '010-77778888', '金逸影城大悦城店位于北京市朝阳区朝阳北路101号朝阳大悦城8层，拥有9个放映厅，包括IMAX厅、杜比影院厅等，是北京市东部的现代化影院。', 1, 4.6, 0, 1);

-- 初始化用户数据
INSERT INTO sys_user (username, password, nickname, email, phone, avatar, role, status) VALUES
('admin', 'admin123', '系统管理员', 'admin@example.com', '13800138000', 'https://picsum.photos/100/100?random=20', 'admin', 1),
('user1', 'user123', '张三', 'user1@example.com', '13800138001', 'https://picsum.photos/100/100?random=21', 'user', 1),
('user2', 'user123', '李四', 'user2@example.com', '13800138002', 'https://picsum.photos/100/100?random=22', 'user', 0);

-- 初始化评论数据
INSERT INTO comment (movie_id, user_id, user_name, avatar, rating, content, images, likes, create_time) VALUES
(1, 1, '电影爱好者', 'https://picsum.photos/50/50?random=10', 5, '特效震撼，剧情紧凑，国产科幻片的巅峰之作！强烈推荐！', NULL, 128, '2025-03-20 14:30:00'),
(1, 2, '星空漫步', 'https://picsum.photos/50/50?random=11', 4, '视觉效果非常棒，故事也很有深度，值得二刷！', NULL, 86, '2025-03-19 20:15:00'),
(2, 1, '影评达人', 'https://picsum.photos/50/50?random=12', 5, '贾玲导演的又一力作，笑中带泪，非常感人！', NULL, 256, '2025-02-10 18:45:00'),
(2, 3, '影迷小王', 'https://picsum.photos/50/50?random=13', 4, '女主角的演技太棒了，剧情很真实！', NULL, 167, '2025-02-09 15:20:00');