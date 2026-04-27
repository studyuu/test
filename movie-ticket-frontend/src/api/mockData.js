export const mockMovies = [
  {
    id: 1,
    title: '流浪地球3',
    englishTitle: 'The Wandering Earth III',
    poster: 'https://picsum.photos/300/450?random=1',
    rating: 9.2,
    duration: 148,
    category: '科幻/冒险',
    releaseDate: '2025-02-10',
    description: '太阳即将毁灭，人类在地球表面建造出巨大的推进器，寻找新的家园。然而宇宙之路危机四伏，为了拯救地球，流浪地球时代的年轻人再次挺身而出，展开争分夺秒的生死之战。',
    director: '郭帆',
    actors: '吴京, 刘德华, 李雪健, 沙溢',
    status: '上映中',
    price: 45
  },
  {
    id: 2,
    title: '热辣滚烫',
    englishTitle: 'YOLO',
    poster: 'https://picsum.photos/300/450?random=2',
    rating: 8.8,
    duration: 129,
    category: '喜剧/剧情',
    releaseDate: '2025-01-29',
    description: '乐莹宅家多年，无所事事。大学毕业工作了一段时间后，她选择脱离社会，封闭社交圈层，这是她认为与自己"和解"的最好方式。一日，在命运的几番"捉弄"下，她决定要换一种方式生活。',
    director: '贾玲',
    actors: '贾玲, 雷佳音, 张小斐, 杨紫',
    status: '上映中',
    price: 42
  },
  {
    id: 3,
    title: '飞驰人生2',
    englishTitle: 'Pegasus 2',
    poster: 'https://picsum.photos/300/450?random=3',
    rating: 8.5,
    duration: 121,
    category: '喜剧/运动',
    releaseDate: '2025-01-29',
    description: '昔日冠军车手张驰沦为落魄驾校教练，过着靠脸吃饭勉强度日的生活。不料天上掉馅饼，濒临停产的老头乐车厂厂长主动提出赞助张驰组建车队再闯最后一届巴音布鲁克拉力赛。',
    director: '韩寒',
    actors: '沈腾, 范丞丞, 尹正, 张本煜',
    status: '上映中',
    price: 40
  },
  {
    id: 4,
    title: '第二十条',
    englishTitle: 'Article 20',
    poster: 'https://picsum.photos/300/450?random=4',
    rating: 8.9,
    duration: 141,
    category: '剧情/喜剧',
    releaseDate: '2025-01-29',
    description: '这一年的不容易谁能懂？自打挂职到市检察院，韩明的糟心事就接二连三。儿子韩雨辰打了校领导儿子并拒绝道歉，妻子李茂娟义愤填膺继而揍了校领导，补刀成功。',
    director: '张艺谋',
    actors: '雷佳音, 马丽, 赵丽颖, 高叶',
    status: '上映中',
    price: 43
  },
  {
    id: 5,
    title: '熊出没·逆转时空',
    englishTitle: 'Boonie Bears: Time Twist',
    poster: 'https://picsum.photos/300/450?random=5',
    rating: 8.3,
    duration: 108,
    category: '动画/喜剧',
    releaseDate: '2025-01-29',
    description: '光头强是一名普通程序员，却常梦见陌生的森林和两头狗熊。直到他跟上司出访大客户，终于想起：他原是森林的小导游，偶然得到一次重新选择人生的机会。',
    director: '林汇达',
    actors: '谭笑, 张秉君, 张伟, 刘思奇',
    status: '上映中',
    price: 35
  },
  {
    id: 6,
    title: '沙丘2',
    englishTitle: 'Dune: Part Two',
    poster: 'https://picsum.photos/300/450?random=6',
    rating: 9.0,
    duration: 166,
    category: '科幻/动作',
    releaseDate: '2025-03-08',
    description: '保罗·厄崔迪公爵加入了弗雷曼人阵营，开始成为他们的精神领袖，并决心对毁灭他家族的阴谋者进行报复。',
    director: '丹尼斯·维伦纽瓦',
    actors: '提莫西·查拉梅, 赞达亚, 丽贝卡·弗格森',
    status: '即将上映',
    price: 48
  }
]

export const mockCinemas = [
  {
    id: 1,
    name: '万达影城（CBD店）',
    address: '北京市朝阳区建国路88号万达广场5层',
    phone: '010-88886666',
    distance: '1.2km',
    rating: 4.8,
    halls: 12,
    services: ['IMAX', '杜比全景声', '4DX', 'VIP厅'],
    price: 45
  },
  {
    id: 2,
    name: 'CGV影城（颐堤港店）',
    address: '北京市朝阳区酒仙桥路18号颐堤港4层',
    phone: '010-66668888',
    distance: '2.5km',
    rating: 4.9,
    halls: 8,
    services: ['IMAX', 'ScreenX', 'SphereX', 'Gold Class'],
    price: 50
  },
  {
    id: 3,
    name: '博纳国际影城（朝阳门店）',
    address: '北京市朝阳区朝阳门外大街18号丰联广场3层',
    phone: '010-55559999',
    distance: '3.1km',
    rating: 4.7,
    halls: 10,
    services: ['IMAX', '中国巨幕', '4D厅'],
    price: 42
  },
  {
    id: 4,
    name: '金逸影城（大悦城店）',
    address: '北京市朝阳区朝阳北路101号朝阳大悦城8层',
    phone: '010-77778888',
    distance: '4.2km',
    rating: 4.6,
    halls: 9,
    services: ['IMAX', '杜比影院', 'VIP厅'],
    price: 40
  }
]

export const mockSchedules = [
  {
    id: 1,
    movieId: 1,
    cinemaId: 1,
    hallName: 'IMAX厅',
    startTime: '2025-03-24 14:30',
    endTime: '2025-03-24 16:58',
    price: 65,
    seats: generateSeats()
  },
  {
    id: 2,
    movieId: 1,
    cinemaId: 1,
    hallName: '2号厅',
    startTime: '2025-03-24 17:00',
    endTime: '2025-03-24 19:28',
    price: 45,
    seats: generateSeats()
  },
  {
    id: 3,
    movieId: 1,
    cinemaId: 1,
    hallName: '3号厅',
    startTime: '2025-03-24 19:30',
    endTime: '2025-03-24 21:58',
    price: 50,
    seats: generateSeats()
  },
  {
    id: 4,
    movieId: 2,
    cinemaId: 2,
    hallName: '1号厅',
    startTime: '2025-03-24 13:00',
    endTime: '2025-03-24 15:09',
    price: 48,
    seats: generateSeats()
  },
  {
    id: 5,
    movieId: 2,
    cinemaId: 2,
    hallName: 'ScreenX厅',
    startTime: '2025-03-24 15:30',
    endTime: '2025-03-24 17:39',
    price: 58,
    seats: generateSeats()
  }
]

export const mockOrders = [
  {
    id: 'ORD202403240001',
    movieTitle: '流浪地球3',
    cinemaName: '万达影城（CBD店）',
    hallName: 'IMAX厅',
    showTime: '2025-03-24 14:30',
    seats: ['5排6座', '5排7座'],
    totalPrice: 130,
    status: '已完成',
    createTime: '2025-03-23 10:30',
    poster: 'https://picsum.photos/300/450?random=1'
  },
  {
    id: 'ORD202403230002',
    movieTitle: '热辣滚烫',
    cinemaName: 'CGV影城（颐堤港店）',
    hallName: '1号厅',
    showTime: '2025-03-23 19:00',
    seats: ['7排8座'],
    totalPrice: 48,
    status: '已取消',
    createTime: '2025-03-22 15:20',
    poster: 'https://picsum.photos/300/450?random=2'
  }
]

export const mockComments = [
  {
    id: 1,
    movieId: 1,
    userName: '电影爱好者',
    avatar: 'https://picsum.photos/50/50?random=10',
    rating: 5,
    content: '特效震撼，剧情紧凑，国产科幻片的巅峰之作！强烈推荐！',
    createTime: '2025-03-20 14:30',
    likes: 128
  },
  {
    id: 2,
    movieId: 1,
    userName: '星空漫步',
    avatar: 'https://picsum.photos/50/50?random=11',
    rating: 4,
    content: '视觉效果非常棒，故事也很有深度，值得二刷！',
    createTime: '2025-03-19 20:15',
    likes: 86
  }
]

function generateSeats() {
  const seats = []
  const rows = 8
  const cols = 12

  for (let r = 1; r <= rows; r++) {
    for (let c = 1; c <= cols; c++) {
      const status = Math.random() > 0.7 ? 'sold' : 'available'
      seats.push({
        row: r,
        col: c,
        rowLabel: String.fromCharCode(64 + r),
        colLabel: c,
        status: status,
        price: 45
      })
    }
  }
  return seats
}


