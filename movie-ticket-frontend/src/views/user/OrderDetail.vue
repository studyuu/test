<template>
  <div class="order-detail-page">
    <div class="container">
      <el-card>
        <template #header>
          <div class="order-header">
            <span>订单详情</span>
            <el-tag type="success">已完成</el-tag>
          </div>
        </template>
        
        <div class="order-info">
          <p><strong>订单号：</strong>{{ $route.params.orderId }}</p>
          <p><strong>下单时间：</strong>2025-03-23 10:30</p>
          <p><strong>影片：</strong>流浪地球3</p>
          <p><strong>影院：</strong>万达影城（CBD店）</p>
          <p><strong>场次：</strong>2025-03-24 14:30 IMAX厅</p>
          <p><strong>座位：</strong>5排6座、5排7座</p>
          <p><strong>总价：</strong><span class="price">¥130</span></p>
        </div>
        
        <div class="order-actions">
          <el-button type="primary">查看取票码</el-button>
          <el-button>申请退票</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.order-detail-page {
  padding: 40px 0;
  background: #f8f9fa;
  min-height: calc(100vh - 70px);
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 40px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-info p {
  margin-bottom: 16px;
  font-size: 15px;
}

.order-info strong {
  color: #666;
  margin-right: 8px;
}

.price {
  font-size: 24px;
  color: #ff6b6b;
  font-weight: bold;
}

.order-actions {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
</style>
