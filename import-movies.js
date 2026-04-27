const axios = require('axios');

// 手动构建mockMovies数组
const mockMovies = [
  {
    "title": "流浪地球3",
    "englishTitle": "The Wandering Earth III",
    "poster": "https://picsum.photos/300/450?random=1",
    "rating": 9.2,
    "duration": 148,
    "category": "科幻/冒险",
    "releaseDate": "2025-02-10",
    "description": "太阳即将毁灭，人类在地球表面建造出巨大的推进器，寻找新的家园。然而宇宙之路危机四伏，为了拯救地球，流浪地球时代的年轻人再次挺身而出，展开争分夺秒的生死之战。",
    "director": "郭帆",
    "actors": "吴京, 刘德华, 李雪健, 沙溢",
    "status": "上映中",
    "price": 45
  },
  {
    "title": "热辣滚烫",
    "englishTitle": "YOLO",
    "poster": "https://picsum.photos/300/450?random=2",
    "rating": 8.8,
    "duration": 129,
    "category": "喜剧/剧情",
    "releaseDate": "2025-01-29",
    "description": "乐莹宅家多年，无所事事。大学毕业工作了一段时间后，她选择脱离社会，封闭社交圈层，这是她认为与自己\"和解\"的最好方式。一日，在命运的几番\"捉弄\"下，她决定要换一种方式生活。",
    "director": "贾玲",
    "actors": "贾玲, 雷佳音, 张小斐, 杨紫",
    "status": "上映中",
    "price": 42
  },
  {
    "title": "飞驰人生2",
    "englishTitle": "Pegasus 2",
    "poster": "https://picsum.photos/300/450?random=3",
    "rating": 8.5,
    "duration": 121,
    "category": "喜剧/运动",
    "releaseDate": "2025-01-29",
    "description": "昔日冠军车手张驰沦为落魄驾校教练，过着靠脸吃饭勉强度日的生活。不料天上掉馅饼，濒临停产的老头乐车厂厂长主动提出赞助张驰组建车队再闯最后一届巴音布鲁克拉力赛。",
    "director": "韩寒",
    "actors": "沈腾, 范丞丞, 尹正, 张本煜",
    "status": "上映中",
    "price": 40
  },
  {
    "title": "第二十条",
    "englishTitle": "Article 20",
    "poster": "https://picsum.photos/300/450?random=4",
    "rating": 8.9,
    "duration": 141,
    "category": "剧情/喜剧",
    "releaseDate": "2025-01-29",
    "description": "这一年的不容易谁能懂？自打挂职到市检察院，韩明的糟心事就接二连三。儿子韩雨辰打了校领导儿子并拒绝道歉，妻子李茂娟义愤填膺继而揍了校领导，补刀成功。",
    "director": "张艺谋",
    "actors": "雷佳音, 马丽, 赵丽颖, 高叶",
    "status": "上映中",
    "price": 43
  },
  {
    "title": "熊出没·逆转时空",
    "englishTitle": "Boonie Bears: Time Twist",
    "poster": "https://picsum.photos/300/450?random=5",
    "rating": 8.3,
    "duration": 108,
    "category": "动画/喜剧",
    "releaseDate": "2025-01-29",
    "description": "光头强是一名普通程序员，却常梦见陌生的森林和两头狗熊。直到他跟上司出访大客户，终于想起：他原是森林的小导游，偶然得到一次重新选择人生的机会。",
    "director": "林汇达",
    "actors": "谭笑, 张秉君, 张伟, 刘思奇",
    "status": "上映中",
    "price": 35
  },
  {
    "title": "沙丘2",
    "englishTitle": "Dune: Part Two",
    "poster": "https://picsum.photos/300/450?random=6",
    "rating": 9.0,
    "duration": 166,
    "category": "科幻/动作",
    "releaseDate": "2025-03-08",
    "description": "保罗·厄崔迪公爵加入了弗雷曼人阵营，开始成为他们的精神领袖，并决心对毁灭他家族的阴谋者进行报复。",
    "director": "丹尼斯·维伦纽瓦",
    "actors": "提莫西·查拉梅, 赞达亚, 丽贝卡·弗格森",
    "status": "即将上映",
    "price": 48
  }
];

// 转换状态字符串为数字
const statusMap = {
  '上映中': 1,
  '即将上映': 2,
  '已下线': 3
};

// 导入影片数据
const importMovies = async () => {
  try {
    console.log('开始导入影片数据...');

    for (const movie of mockMovies) {
      // 转换状态
      const status = statusMap[movie.status] || 1;

      // 构建影片对象
      const movieData = {
        title: movie.title,
        englishTitle: movie.englishTitle,
        category: movie.category,
        duration: movie.duration,
        rating: movie.rating,
        status: status,
        poster: movie.poster,
        director: movie.director,
        actors: movie.actors,
        releaseDate: movie.releaseDate,
        price: movie.price,
        synopsis: movie.description || movie.synopsis
      };

      // 调用后端接口 - 使用标准的 /api/movies 接口
      console.log(`正在导入影片 ${movie.title}...`);
      const response = await axios.post('http://localhost:8080/api/movies', movieData);
      console.log(`导入影片 ${movie.title} 成功:`, response.data);
    }

    console.log('影片数据导入完成！');
  } catch (error) {
    console.error('导入影片数据失败:', error.response ? error.response.data : error.message);
  }
};

// 执行导入
importMovies();

