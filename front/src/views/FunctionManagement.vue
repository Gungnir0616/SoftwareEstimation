<template>
    <el-container>
      <el-header>
        <MyHeader :breadcrumb="breadcrumb" :title="title"></MyHeader>
      </el-header>
      <el-divider />
  <el-container>
    <!-- 左侧边栏 -->
    <el-aside width="200px">
      <Aside />
    </el-aside>

    <!-- 右侧主页面 -->
    <el-container>
      <el-main>
        <!-- 标题卡片 -->
        <el-card>
          <template #header>
            <div class="card-header">
              <span class="header-title">功能点分析管理页面</span>
            </div>
          </template>
          <!-- =====================================================项目列表============================================================= -->
          <el-table
            :data="projects"
            border
            style="width: 100%; margin-top: 20px;"
          >
            <!-- Project ID -->
            <el-table-column prop="id" label="ID" width="50" />
            
            <!-- Name -->
            <el-table-column prop="name" label="Name" width="100" />

            <!-- Owner -->
            <el-table-column prop="owner" label="Owner" width="100" />

            <!-- Requirements -->
            <el-table-column prop="requirements" label="Requirements" />

            <!-- Operation -->
            <el-table-column label="Operation" width="370">
              <template #default="scope">
                <el-button
                  type="primary"
                  size="small"
                  @click="openDialog('details', scope.row)"
                >
                  查看详情
                </el-button>
                <el-button
                  type="success"
                  size="small"
                  @click="openDialog('functions', scope.row)"
                >
                  功能点管理
                </el-button>
                <el-button
                  type="info"
                  size="small"
                  @click="openDialog('results', scope.row)"
                >
                  查看结果
                </el-button>
                <el-button
                  type="warning"
                  size="small"
                  @click="exportResults(scope.row.id)"
                >
                  导出结果
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
        
        <!-- =====================================================通用矩形弹窗====================================== -->
        <div
          v-if="dialogVisible"
          class="small-dialog"
        >
          <div class="dialog-header">
            <span>{{ dialogTitle }}</span>
            <el-button
              type="text"
              size="small"
              icon="el-icon-arrow-left"
              @click="closeDialog"
            >
              返回
            </el-button>
          </div>
        <!-- 动态内容 -->
        <div class="dialog-content">
        <!-- =====================================================查看详情====================================== -->
          <template v-if="dialogType === 'details'">
            <h3>需求详情</h3>
            <p><strong>项目 ID：</strong> {{ selectedProject.id }}</p>
            <p><strong>项目名称：</strong> {{ selectedProject.name }}</p>
            <p><strong>项目负责人：</strong> {{ selectedProject.owner }}</p>
            <p><strong>需求描述：</strong> {{ selectedProject.requirements }}</p>
          </template>
        <!-- =====================================================功能点管理====================================== -->
          <template v-if="dialogType === 'functions'">
            <!-- 需求详情区域 -->
            <div class="requirements-section">
              <h3>需求详情</h3>
              <p>{{ selectedProject.requirements }}</p>
            </div>

            <!-- 功能点操作按钮 -->
            <div class="dialog-actions">
              <el-button type="primary" size="small" @click="switchToResults">
                计算功能点
              </el-button>
              <el-button type="success" size="small" @click="exportFunctionPoints">
                导出功能点
              </el-button>
              <el-upload
                accept=".csv"
                :before-upload="importFunctionPoints"
                :show-file-list="false">
                <el-button type="primary" size="small">导入功能点</el-button>
              </el-upload>
              <el-button type="warning" size="small" @click="handleAdd">
                添加功能点
              </el-button>
            </div>

            <el-table
              :data="[...(functionPoints || []), ...(addingFunctionPoint ? [newFunctionPoint] : [])]"
              border
              style="width: 100%; margin-top: 10px;"
            >
              <!-- Name 列 -->
              <el-table-column prop="name" label="Name">
                <template #default="scope">
                  <el-input
                    v-if="addingFunctionPoint && scope.row === newFunctionPoint"
                    v-model="newFunctionPoint.name"
                    placeholder="输入名称"
                  />
                  <el-input
                    v-else-if="editingRowId === scope.row.id"
                    v-model="editedRowData.name"
                    placeholder="输入名称"
                  />
                  <span v-else>{{ scope.row.name }}</span>
                </template>
              </el-table-column>

              <!-- Type 列 -->
              <el-table-column prop="type" label="Type" width="120">
                <template #default="scope">
                  <el-select
                    v-if="addingFunctionPoint && scope.row === newFunctionPoint"
                    v-model="newFunctionPoint.type"
                    placeholder="选择类型"
                  >
                    <el-option label="ILF" value="ILF" />
                    <el-option label="EIF" value="EIF" />
                    <el-option label="EI" value="EI" />
                    <el-option label="EO" value="EO" />
                    <el-option label="EQ" value="EQ" />
                  </el-select>
                  <el-select
                    v-else-if="editingRowId === scope.row.id"
                    v-model="editedRowData.type"
                    placeholder="选择类型"
                  >
                    <el-option label="ILF" value="ILF" />
                    <el-option label="EIF" value="EIF" />
                    <el-option label="EI" value="EI" />
                    <el-option label="EO" value="EO" />
                    <el-option label="EQ" value="EQ" />
                  </el-select>
                  <span v-else>{{ scope.row.type }}</span>
                </template>
              </el-table-column>

              <!-- Complexity 列 -->
              <el-table-column prop="complexity" label="Complexity" width="120">
                <template #default="scope">
                  <el-select
                    v-if="addingFunctionPoint && scope.row === newFunctionPoint"
                    v-model="newFunctionPoint.complexity"
                    placeholder="选择复杂度"
                  >
              <el-option label="LOW" value="LOW" />
              <el-option label="MEDIUM" value="MEDIUM" />
              <el-option label="HIGH" value="HIGH" />
                  </el-select>
                  <el-select
                    v-else-if="editingRowId === scope.row.id"
                    v-model="editedRowData.complexity"
                    placeholder="选择复杂度"
                  >
              <el-option label="LOW" value="LOW" />
              <el-option label="MEDIUM" value="MEDIUM" />
              <el-option label="HIGH" value="HIGH" />
                  </el-select>
                  <span v-else>{{ scope.row.complexity }}</span>
                </template>
              </el-table-column>

              <!-- Comment 列 -->
              <el-table-column prop="comment" label="Comment">
                <template #default="scope">
                  <el-input
                    v-if="addingFunctionPoint && scope.row === newFunctionPoint"
                    v-model="newFunctionPoint.comment"
                    placeholder="输入备注"
                  />
                  <el-input
                    v-else-if="editingRowId === scope.row.id"
                    v-model="editedRowData.comment"
                    placeholder="输入备注"
                  />
                  <span v-else>{{ scope.row.comment }}</span>
                </template>
              </el-table-column>

              <!-- 操作列 -->
              <el-table-column label="Operation" width="140">
                <template #default="scope">
                  <!-- 新增功能点 -->
                  <div v-if="addingFunctionPoint && scope.row === newFunctionPoint">
                    <el-button type="success" size="small" @click="saveNewFunctionPoint">
                      保存
                    </el-button>
                    <el-button type="danger" size="small" @click="cancelAddFunctionPoint">
                      取消
                    </el-button>
                  </div>

                  <!-- 编辑功能点 -->
                  <div v-else-if="editingRowId === scope.row.id">
                    <el-button type="success" size="small" @click="saveEditedRow(scope.row.id)">
                      保存
                    </el-button>
                    <el-button type="danger" size="small" @click="cancelEditRow">
                      取消
                    </el-button>
                  </div>

                  <!-- 默认操作 -->
                  <div v-else>
                    <el-button type="text" size="small" @click="handleEdit(scope.row.id)">
                      修改
                    </el-button>
                    <el-button type="text" size="small" @click="handleDelete(scope.row.id)">
                      删除
                    </el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </template>

        <!-- ===============================================查看结果====================================== -->
          <template v-if="dialogType === 'results'">
            <!-- <div class="requirements-section">
              <h3>需求详情</h3>
              <p>{{ selectedProject.requirements }}</p>
            </div> -->

            <el-row :gutter="20">
              <!-- 功能点类型统计与计算结果 -->
              <el-col :span="12">
                <div class="summary-section">
                  <h3>功能点类型统计与计算结果</h3>
                  <el-table :data="functionTypeSummary" border>
                    <el-table-column prop="category" label="类型" />
                    <el-table-column prop="value" label="值" />
                  </el-table>
                </div>
                <div class="adjust-button-section" style="margin-top: 20px;">
                  <el-button type="warning" size="small" @click="switchToFunctions">
                    调整功能点
                  </el-button>
                </div>
              </el-col>

              <!-- 调整因子设置 -->
              <el-col :span="12">
                <div class="adjustment-section">
                  <h3>调整因子设置</h3>
                  <el-form>
                    <!-- 选择调整因子 -->
                    <el-radio-group v-model="selectedAdjustment" @change="openAdjustmentDialog">
                      <el-radio label="SCF">规模控制因子 (SCF)</el-radio>
                      <el-radio label="GSC">一般系统特性 (GSC)</el-radio>
                    </el-radio-group>
                  </el-form>

                  <!-- 动态显示选中调整因子的值 -->
                  <div v-if="selectedAdjustment" style="margin-top: 20px;">
                    <p><strong>选中调整因子：</strong> {{ selectedAdjustment }}</p>
                    <p v-if="selectedAdjustment === 'GSC'">
                      <p><strong>GSC 总值：</strong> {{ gscTotal }}</p>
                      <strong>VAF 总值：</strong> {{ vaf.toFixed(2) }}
                    </p>
                    <p v-if="selectedAdjustment === 'SCF'">
                      <strong>SCF 调整系数：</strong>
                      <span>
                        {{ scfStages.find(stage => stage.value === Number(selectedScfStage))?.factor || '未知系数' }}
                      </span>
                    </p>
                    <p>
                      <strong>调整后功能点数 (AFP)：</strong>
                      <span v-if="selectedAdjustment === 'SCF'">
                        {{ scfAfp ? scfAfp.toFixed(2) : '未计算' }}
                      </span>
                      <span v-else-if="selectedAdjustment === 'GSC'">
                        {{ gscAfp ? gscAfp.toFixed(2) : '未计算' }}
                      </span>
                      <span v-else>未选择调整因子</span>
                    </p>
                    <el-button type="primary" size="small" @click="openAdjustmentDialog">
                      修改 {{ selectedAdjustment }} 参数
                    </el-button>
                  </div>
                </div>
              </el-col>
            </el-row>
            <el-divider />
            <div class="chart-section">
              <!-- <h3>功能点计算记录变化</h3> -->
              <div id="calculation-chart" style="width: 100%; height: 400px;"></div>
            </div>
          
            <!-- 弹出框 -->
            <el-dialog
              v-model="isDialogVisible"
              :title="selectedAdjustment === 'SCF' ? '规模控制因子 (SCF)' : '一般系统特性 (GSC)'"
              width="600px"
              @open="handleDialogOpen"
              @close="closeAdjustmentDialog"
            >
              <!-- GSC 因子表 -->
              <template v-if="selectedAdjustment === 'GSC'">
                <el-table ref="gscTable" :data="gscData" border>
                  <el-table-column prop="id" label="序号" width="50" />
                  <el-table-column prop="name" label="GSC 因子" />
                  <el-table-column prop="description" label="描述" />
                  <el-table-column label="DI 取值">
                    <template #default="scope">
                      <el-select v-model="scope.row.value" placeholder="选择取值">
                        <el-option
                          v-for="n in 6"
                          :key="n"
                          :value="n - 1"
                          :label="n - 1"
                        ></el-option>
                      </el-select>
                    </template>
                  </el-table-column>
                </el-table>
              </template>

              <!-- SCF 阶段选择 -->
              <template v-if="selectedAdjustment === 'SCF'">
                <el-radio-group v-model="selectedScfStage">
                  <el-radio label="0">项目立项</el-radio>
                  <el-radio label="1">项目招标</el-radio>
                  <el-radio label="2">项目早期</el-radio>
                  <el-radio label="3">项目中期</el-radio>
                  <el-radio label="4">项目完成</el-radio>
                </el-radio-group>
              </template>

              <!-- 弹窗底部 -->
              <template #footer>
                <el-button @click="closeAdjustmentDialog">取消</el-button>
                <el-button type="primary" @click="confirmAdjustment">确认</el-button>
              </template>
            </el-dialog>
          </template>
          </div>
        </div>
      </el-main>
    </el-container>
  </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onUnmounted, watch } from "vue";
import axios from "axios";
import Aside from "@/components/Aside.vue";
import MyHeader from '@/components/MyHeader.vue'
import { ElMessage } from "element-plus";
import * as echarts from 'echarts';


// ========================================总页面==================================================================
const userId = 1;
const projects = ref([]);

const dialogVisible = ref(false); 
const dialogType = ref("");
const dialogTitle = ref("");
const selectedProject = ref({
  id: null,
  requirements: "",
});
const ufp = ref(null);

// API URL 前缀
const API_BASE_URL = "http://120.53.31.148:6757";
// const API_BASE_URL = "http://127.0.0.1:5000";


// 页面加载时获取项目数据
onMounted(async () => {
  console.log("Fetching projects...");
  await fetchProjects();
  console.log("Projects loaded:", projects.value);
});



// 获取项目数据
const fetchProjects = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/projects`, {
      params: { user_id: userId },
    });
    projects.value = response.data.projects.map((project) => ({
      id: project.id,
      name: project.name,
      owner: project.owner_name,
      requirements: project.requirement,
    }));
  } catch (error) {
    console.error("获取项目列表失败：", error);
    ElMessage.error("无法加载项目列表，请检查后端服务是否运行！");
  }
};

// 打开弹窗
const openDialog = (type, project) => {
  console.log("Selected Project:", project);
  selectedProject.value = { ...project }; 
  dialogType.value = type; 
  dialogTitle.value = type === "details" ? "需求详情" :
                      type === "functions" ? "功能点管理" :
                      "查看结果"; 
  dialogVisible.value = true;

  // 重置变量
  if (type === "results") {
    selectedAdjustment.value = ""; // 重置调整因子选择
    selectedScfStage.value = 0;    // 重置 SCF 阶段
    gscTotal.value = 0;            // 重置 GSC 总值
    ufp.value = null;              // 清空未调整功能点数
    scfAfp.value = 0;              // 清空 SCF 调整功能点数
    gscAfp.value = 0;              // 清空 GSC 调整功能点数
    vaf.value = 0;                 // 清空 VAF

    fetchFunctionPoints(project.id); // 加载功能点
    fetchTotalFunctionPoint(project.id, gscTotal.value, selectedScfStage.value); // 加载计算结果
    fetchCalculationResults(); // 加载计算记录
  } else if (type === "functions") {
    fetchFunctionPoints(project.id); // 如果为功能点管理界面，加载功能点数据
  }
};

// 关闭弹窗
const closeDialog = () => {
  dialogVisible.value = false;
};


// ======================================功能点管理==============================================================
const functionPoints = ref([]);
const editingRowId = ref(null);
const editedRowData = ref({});
const addingFunctionPoint = ref(false);

// 临时存储新功能点的数据
const newFunctionPoint = ref({
  id: null,
  name: "",
  type: "",
  complexity: "",
  comment: "",
});

// 获取功能点表数据
const fetchFunctionPoints = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/get-functionpoint`, {
      params: { project_id: selectedProject.value.id },
    });

    // 解析返回的数据
    functionPoints.value = response.data.function_points.map((point) => ({
      id: point.id,
      name: point.name,
      type: point.type,
      complexity: point.complexity,
      comment: point.comment,
    }));
  } catch (error) {
    console.error("获取功能点失败：", error);
    ElMessage.error("无法加载功能点数据！");
  }
};


// 导出功能点
const exportFunctionPoints = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/export-functionpoints`, {
      params: { project_id: selectedProject.value.id },
      responseType: "blob", // 以 Blob 格式接收文件数据
    });

    // 创建一个链接用于下载文件
    const blob = new Blob([response.data], { type: "text/csv" });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", `${selectedProject.value.name}_functionpoints.csv`);
    document.body.appendChild(link);
    link.click();
    link.remove();

    ElMessage.success("功能点导出成功！");
  } catch (error) {
    console.error("导出功能点失败：", error);
    ElMessage.error("无法导出功能点，请检查后端服务！");
  }
};

// 导入功能点
const importFunctionPoints = async (file) => {
  const formData = new FormData();
  formData.append("project_id", selectedProject.value.id);
  formData.append("file", file);

  try {
    const response = await axios.post(`${API_BASE_URL}/import-functionpoints`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    ElMessage.success(`成功导入功能点数量：${response.data.count}`);
    await fetchFunctionPoints(); // 刷新功能点列表
  } catch (error) {
    if (error.response) {
      console.error("导入功能点失败：", error.response.data);
      ElMessage.error(`导入失败：${error.response.data.error || "未知错误"}`);
    } else {
      console.error("导入功能点失败：", error);
      ElMessage.error("无法导入功能点，请检查后端服务！");
    }
  }
};

const handleAdd = () => {
  addingFunctionPoint.value = true; // 进入添加模式
  newFunctionPoint.value = {
    name: "", // 初始化名称
    type: "", // 初始化类型
    complexity: "", // 初始化复杂度
    comment: "", // 初始化备注
  };
};

const handleEdit = (id) => {
  const row = functionPoints.value.find((item) => item.id === id);
  if (row) {
    editingRowId.value = id; // 将当前行 ID 设置为正在编辑
    editedRowData.value = { ...row }; // 复制当前行数据
  } else {
    console.error("未找到指定功能点 ID 的数据：", id);
  }
};

const handleDelete = async (id) => {
  try {
    await axios.delete(`${API_BASE_URL}/delete-functionpoint`, {
      data: { id },
    });
    functionPoints.value = functionPoints.value.filter((item) => item.id !== id);
    ElMessage.success(`功能点删除成功：ID ${id}`);
  } catch (error) {
    console.error("删除功能点失败：", error);
    ElMessage.error("无法删除功能点，请检查后端服务！");
  }
};

// 保存添加功能点
const saveNewFunctionPoint = async () => {
  // 数据验证和格式化
  const type = newFunctionPoint.value.type.toUpperCase();
  const complexity = newFunctionPoint.value.complexity.toUpperCase();

  if (!['ILF', 'EIF', 'EI', 'EO', 'EQ'].includes(type)) {
    ElMessage.error("无效的功能点类型！");
    return;
  }

  if (!['LOW', 'MEDIUM', 'HIGH'].includes(complexity)) {
    ElMessage.error("无效的功能点复杂度！");
    return;
  }

  console.log("发送的数据：", {
    project_id: selectedProject.value.id,
    name: newFunctionPoint.value.name,
    type,
    complexity,
    comment: newFunctionPoint.value.comment,
  });

  try {
    const response = await axios.post(`${API_BASE_URL}/add-functionpoint`, {
      project_id: selectedProject.value.id,
      name: newFunctionPoint.value.name,
      type,
      complexity,
      comment: newFunctionPoint.value.comment,
    });
    newFunctionPoint.value.id = response.data.id; // 返回 ID
    functionPoints.value.push({ ...newFunctionPoint.value });
    addingFunctionPoint.value = false;
    ElMessage.success("功能点添加成功！");
  } catch (error) {
    console.error("添加功能点失败：", error);
    ElMessage.error(error.response?.data?.error || "无法添加功能点，请检查后端服务！");
  }
};

// 保存编辑的功能点
const saveEditedRow = async (id) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/edit-functionpoint`, {
      id,
      ...editedRowData.value,
    });

    const index = functionPoints.value.findIndex((item) => item.id === id);
    if (index !== -1) {
      functionPoints.value[index] = { ...editedRowData.value };
      ElMessage.success("功能点修改成功！");
    }
    editingRowId.value = null; // 退出编辑模式
    editedRowData.value = {}; // 清空编辑缓存
  } catch (error) {
    console.error("修改功能点失败：", error);
    ElMessage.error("无法修改功能点，请检查后端服务！");
  }
};

// 取消添加功能点
const cancelAddFunctionPoint = () => {
  addingFunctionPoint.value = false;
  newFunctionPoint.value = {};
};

const cancelEditRow = () => {
  editingRowId.value = null; // 清空编辑状态
  editedRowData.value = {}; // 清空编辑缓存
  ElMessage.info("编辑已取消");
};

// 跳转到查看结果页面
const switchToResults = async () => {
  try {
    dialogType.value = "results"; // 切换到结果页面
    await fetchTotalFunctionPoint(
      selectedProject.value.id,
      gscTotal.value,
      selectedScfStage.value
    );
    await fetchCalculationResults();
    ElMessage.success("结果已成功加载！");
  } catch (error) {
    console.error("切换到查看结果页面失败：", error);
    ElMessage.error("无法加载结果，请检查后端服务！");
  }
};


// =======================================查看结果===============================================================
// GSC 因子数据
const gscData = ref([
  { id: 1, name: "数据通信", description: "data communication", value: 0 },
  { id: 2, name: "分布式数据处理", description: "distributed data processing", value: 0 },
  { id: 3, name: "性能", description: "performance", value: 0 },
  { id: 4, name: "重度配置", description: "heavily used configuration", value: 0 },
  { id: 5, name: "处理速率", description: "transaction rate", value: 0 },
  { id: 6, name: "在线数据输入", description: "online data entry", value: 0 },
  { id: 7, name: "终端用户使用效率", description: "end user efficiency", value: 0 },
  { id: 8, name: "在线升级", description: "online update", value: 0 },
  { id: 9, name: "复杂处理", description: "complex processing", value: 0 },
  { id: 10, name: "可重用性", description: "reusability", value: 0 },
  { id: 11, name: "易安装性", description: "installation ease", value: 0 },
  { id: 12, name: "易操作性", description: "operation ease", value: 0 },
  { id: 13, name: "操作场所", description: "multiple sites", value: 0 },
  { id: 14, name: "支持变更", description: "facilitate change", value: 0 },
]);

// 选中的调整因子
const scfAfp = ref(0); // SCF 调整后的功能点数
const gscAfp = ref(0); // GSC 调整后的功能点数
const vaf = ref(0);    // 权重调整因子
const selectedAdjustment = ref("");
const selectedScfStage = ref(0); 
const gscTotal = computed(() => gscData.value.reduce((sum, item) => sum + item.value, 0));

// 调整后的功能点数 (AFP)
const afp = ref(0);

// 弹窗控制
const isDialogVisible = ref(false);

// 弹窗表格引用
const gscTable = ref(null);

// 打开弹窗时手动调整表格布局
const handleDialogOpen = async () => {
  await nextTick(); // 确保 DOM 完全渲染
  if (gscTable.value) {
    gscTable.value.doLayout(); // 强制刷新表格布局
  }
};

// 打开弹窗
const openAdjustmentDialog = () => {
  if (!selectedAdjustment.value) {
    ElMessage.warning("请先选择调整因子！");
    return;
  }
  isDialogVisible.value = true;
};

// 关闭弹窗
const closeAdjustmentDialog = () => {
  isDialogVisible.value = false;
};


// 切换到功能点管理页面
const switchToFunctions = () => {
  dialogType.value = "functions";
  dialogTitle.value = "功能点管理";
};

const scfStages = ref([
  { name: "项目立项", value: 0, factor: 2.00 },
  { name: "项目招标", value: 1, factor: 1.50 },
  { name: "项目早期", value: 2, factor: 1.26 },
  { name: "项目中期", value: 3, factor: 1.26 },
  { name: "项目完成", value: 4, factor: 1.00 },
]);

// 获取功能点类型统计，并添加 UFP 值
const functionTypeSummary = computed(() => {
  const summary = {};

  functionPoints.value.forEach((point) => {
    summary[point.type] = (summary[point.type] || 0) + 1;
  });

  const summaryArray = Object.keys(summary).map((type) => ({
    category: type,
    value: summary[type],
  }));

  if (ufp.value !== null) {
    summaryArray.push({ category: "UFP", value: ufp.value });
  }

  return summaryArray;
});

// 获取功能点统计和总数
const fetchTotalFunctionPoint = async (projectId, totalWeight = 0, scfStage = 4) => {
  try {
    console.log("发送请求数据:", { projectId, totalWeight, scfStage });

    const response = await axios.post(`${API_BASE_URL}/cal-functionpoint`, {
      project_id: projectId,
      total_weight: totalWeight,
      scf_stage: Number(selectedScfStage.value) || 0,
    });

    const { UFP, SCF_AFP, GSC_AFP, VAF } = response.data;

    // 更新 Vue 数据
    ufp.value = UFP;
    scfAfp.value = SCF_AFP;
    gscAfp.value = GSC_AFP;
    vaf.value = VAF;

    console.log("功能点计算结果:", { UFP, SCF_AFP, GSC_AFP, VAF });
  } catch (error) {
    console.error("功能点计算失败：", error.response?.data || error.message);
    throw new Error("无法获取功能点计算结果");
  }
};

// 计算功能点（单次更新）
const calculateFunctionPoint = async ({ project_id, total_weight, scfStage }) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/cal-functionpoint`, {
      project_id,
      total_weight: total_weight || 0,
      scf_stage: Number(scfStage) || 0, // 转换为数值类型
    });

    // 解析返回数据
    const { UFP, SCF_AFP, GSC_AFP, VAF } = response.data;

    // 更新前端数据
    ufp.value = UFP || 0;
    scfAfp.value = SCF_AFP || 0;
    gscAfp.value = GSC_AFP || 0;
    vaf.value = VAF || 0;

    console.log("功能点重新计算完成：", { UFP, SCF_AFP, GSC_AFP, VAF });
  } catch (error) {
    console.error("功能点计算失败：", error.response?.data || error.message);
    throw new Error("功能点计算失败，请检查后端服务！");
  }
};

// 确认调整因子
const confirmAdjustment = async () => {
  try {
    if (!selectedAdjustment.value) {
      ElMessage.warning("请选择调整因子！");
      return;
    }

    // 确认传递的参数和逻辑
    const params = {
      project_id: selectedProject.value.id,
      total_weight: gscTotal.value || 0, 
      scfStage: Number(selectedScfStage.value) || 0,
    };

    console.log("发送请求参数:", params);

    await calculateFunctionPoint(params);

    // 调整成功后获取最新的计算结果记录
    await fetchCalculationResults();

    // 成功提示
    ElMessage.success("调整成功，功能点计算完成！");
  } catch (error) {
    console.error("调整失败：", error.response?.data || error.message);
    ElMessage.error("调整失败，请检查输入或后端服务！");
  } finally {
    isDialogVisible.value = false;
  }
};

// =============================================导出结果========================================================
// 导出总结果
const exportResults = async (projectId) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/export-results`, {
      params: { project_id: projectId },
    });

    const { function_points_csv, calculation_records_csv } = response.data;

    // 创建 Blob 并生成下载链接
    const functionPointsBlob = new Blob([function_points_csv], {
      type: "text/csv;charset=utf-8;",
    });
    const calculationRecordsBlob = new Blob([calculation_records_csv], {
      type: "text/csv;charset=utf-8;",
    });

    // 下载功能点文件
    const functionPointsUrl = window.URL.createObjectURL(functionPointsBlob);
    const functionPointsLink = document.createElement("a");
    functionPointsLink.href = functionPointsUrl;
    functionPointsLink.setAttribute(
      "download",
      `Project_${projectId}_FunctionPoints.csv`
    );
    document.body.appendChild(functionPointsLink);
    functionPointsLink.click();
    functionPointsLink.remove();

    // 下载计算记录文件
    const calculationRecordsUrl = window.URL.createObjectURL(calculationRecordsBlob);
    const calculationRecordsLink = document.createElement("a");
    calculationRecordsLink.href = calculationRecordsUrl;
    calculationRecordsLink.setAttribute(
      "download",
      `Project_${projectId}_CalculationRecords.csv`
    );
    document.body.appendChild(calculationRecordsLink);
    calculationRecordsLink.click();
    calculationRecordsLink.remove();

    ElMessage.success("结果导出成功！");
  } catch (error) {
    console.error("导出结果失败：", error);
    ElMessage.error(
      error.response?.data?.error || "无法导出结果，请检查后端服务！"
    );
  }
};

// ========================================图表显示==============================================================
const calculationResults = ref([]); // 用于存储多次计算记录

const fetchCalculationResults = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/get-calculations`, {
      params: { project_id: selectedProject.value.id },
    });

    // 检查返回数据是否包含 VAF 和 UFP
    calculationResults.value = response.data.map(item => ({
      created_at: item.created_at,
      SCF_AFP: item.SCF_AFP,
      GSC_AFP: item.GSC_AFP,
      VAF: item.VAF || 0, // 默认为 0
      UFP: item.UFP || 0, // 默认为 0
    }));

    // 重新渲染图表
    renderCalculationChart();

    console.log("计算记录获取成功:", calculationResults.value);
  } catch (error) {
    console.error("获取计算记录失败：", error);
    ElMessage.error("无法加载计算记录，请检查后端服务！");
  }
};

// 渲染计算结果的 ECharts 图表
const renderCalculationChart = () => {
  const chartDom = document.getElementById("calculation-chart");
  if (!chartDom) return;

  const myChart = echarts.init(chartDom);

  // 数据准备
  const dates = calculationResults.value.map((item) => item.created_at); // 时间戳
  const scfAfpData = calculationResults.value.map((item) => item.SCF_AFP); // SCF 调整功能点数
  const gscAfpData = calculationResults.value.map((item) => item.GSC_AFP); // GSC 调整功能点数
  const vafData = calculationResults.value.map((item) => item.VAF); // 权重调整因子
  const ufpData = calculationResults.value.map((item) => item.UFP); // 未调整功能点数

  // 配置图表
  const option = {
    title: {
      text: "功能点计算结果变化",
      left: "center",
    },
    tooltip: {
      trigger: "axis",
    },
    legend: {
      data: ["SCF_AFP", "GSC_AFP", "VAF", "UFP"], // 添加 VAF 和 UFP
      top: "10%",
    },
    xAxis: {
      type: "category",
      data: dates,
    },
    yAxis: {
      type: "value",
    },
    series: [
      {
        name: "SCF_AFP",
        type: "line",
        data: scfAfpData,
      },
      {
        name: "GSC_AFP",
        type: "line",
        data: gscAfpData,
      },
      {
        name: "VAF",
        type: "line",
        data: vafData,
      },
      {
        name: "UFP",
        type: "line",
        data: ufpData,
      },
    ],
  };

  // 渲染图表
  myChart.setOption(option);
};

// 在计算记录数据更新后重新渲染图表
watch(calculationResults, () => {
  nextTick(() => renderCalculationChart());
});


</script>


<!-- ------------------------------------------------------- -->
<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 20px;
  font-weight: bold;
}

.small-dialog {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 800px;
  z-index: 1000;
  background-color: white;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  border-radius: 8px;
  padding: 20px;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
}

.dialog-content {
  margin-bottom: 20px;
}

.dialog-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  justify-content: flex-start;
}

.requirements-section {
  margin-bottom: 20px;
  padding: 10px;
  border: 1px solid #ebeef5;
  border-radius: 5px;
  background-color: #f5f7fa;
}

.summary-section,
.ufp-section,
.adjustment-section {
  margin-bottom: 20px;
}

h3 {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
}

.el-dialog {
  width: 600px;
}

.el-table {
  width: 100%;
}

.chart-section #calculation-chart {
  width: 100%;
  height: 200px;
}
</style>
