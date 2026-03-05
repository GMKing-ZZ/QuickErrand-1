<template>
  <div class="page-header">
    <div class="header-content">
      <div class="header-left">
        <div class="header-icon-wrapper">
          <el-icon class="header-icon">
            <component :is="icon" />
          </el-icon>
        </div>
        <div class="header-text-wrapper">
          <h1 class="header-title">{{ title }}</h1>
          <p class="header-subtitle" v-if="subtitle">{{ subtitle }}</p>
        </div>
      </div>
      <div class="header-right" v-if="$slots.extra || stats.length > 0">
        <slot name="extra"></slot>
        <div class="header-stats" v-if="stats.length > 0">
          <div class="stat-item" v-for="(stat, index) in stats" :key="index">
            <span class="stat-label">{{ stat.label }}</span>
            <span class="stat-value">{{ stat.value }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  subtitle: {
    type: String,
    default: ''
  },
  icon: {
    type: Object,
    required: true
  },
  stats: {
    type: Array,
    default: () => []
  }
})
</script>

<style scoped>
.page-header {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border-radius: var(--qe-radius-xl);
  margin-bottom: 24px;
  box-shadow: var(--qe-primary-shadow);
  overflow: hidden;
  position: relative;
  transition: all var(--qe-transition-base);
}

html.dark .page-header {
  background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%);
  box-shadow: 0 4px 14px rgba(30, 64, 175, 0.3);
}

.page-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.08) 0%, transparent 60%);
  pointer-events: none;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28px 36px;
  position: relative;
  z-index: 1;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
  flex: 1;
}

.header-icon-wrapper {
  width: 64px;
  height: 64px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--qe-radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all var(--qe-transition-base);
}

.header-icon-wrapper:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
}

.header-icon {
  font-size: 32px;
  color: #ffffff;
}

.header-text-wrapper {
  flex: 1;
}

.header-title {
  font-size: 26px;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 8px 0;
  line-height: 1.2;
  letter-spacing: 0.3px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-subtitle {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
  line-height: 1.5;
  font-weight: 400;
  letter-spacing: 0.2px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.header-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: var(--qe-radius-lg);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all var(--qe-transition-base);
}

.stat-item:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 4px;
  font-weight: 500;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #ffffff;
  line-height: 1.2;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
    padding: 20px 24px;
  }

  .header-right {
    width: 100%;
    justify-content: flex-start;
  }

  .header-stats {
    width: 100%;
  }

  .stat-item {
    flex: 1;
    align-items: center;
  }
}
</style>
