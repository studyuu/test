<template>
  <div class="pagination-wrapper">
    <el-pagination
      v-model:current-page="currentPageModel"
      v-model:page-size="pageSizeModel"
      :total="total"
      :page-sizes="pageSizes"
      :layout="layout"
      size="small"
      background
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  currentPage: {
    type: Number,
    default: 1
  },
  pageSize: {
    type: Number,
    default: 10
  },
  total: {
    type: Number,
    default: 0
  },
  pageSizes: {
    type: Array,
    default: () => [10, 20, 50, 100]
  },
  layout: {
    type: String,
    default: 'total, sizes, prev, pager, next, jumper'
  }
})

const emit = defineEmits(['update:currentPage', 'update:pageSize', 'change'])

const currentPageModel = computed({
  get: () => props.currentPage,
  set: (val) => emit('update:currentPage', val)
})

const pageSizeModel = computed({
  get: () => props.pageSize,
  set: (val) => emit('update:pageSize', val)
})

const handleSizeChange = (val) => {
  emit('change', { pageNum: props.currentPage, pageSize: val })
}

const handleCurrentChange = (val) => {
  emit('change', { pageNum: val, pageSize: props.pageSize })
}
</script>

<style scoped>
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding: 30px 0 0 0;
}
</style>
