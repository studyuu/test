import { mockMovies } from './mockData.js'
import { movieAPI } from './api.js'

// 转换状态字符串为数字
const statusMap = {
  '上映中': 1,
  '即将上映': 2,
  '已下线': 3
}

// 导入影片数据
const importMovies = async () => {
  try {
    console.log('开始导入影片数据...')

    for (const movie of mockMovies) {
      // 转换状态
      const status = statusMap[movie.status] || 1

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
      }

      // 调用后端接口
      const response = await movieAPI.addMovie(movieData)
      console.log(`导入影片 ${movie.title} 成功:`, response.data)
    }

    console.log('影片数据导入完成！')
  } catch (error) {
    console.error('导入影片数据失败:', error)
  }
}

// 执行导入
importMovies()
