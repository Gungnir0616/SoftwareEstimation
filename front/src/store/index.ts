import { createStore } from 'vuex'
import { State, User } from './types';
// @ts-ignore
import createPersistedState from 'vuex-persistedstate'
export default createStore<State>({
  state: {
    user: null,
  },
  getters: {
    isSuperAdmin: (state) => {
      return state.user?.role === '超级管理员';
    },
    userName: (state) => state.user ? state.user.userName : '',
  },
  mutations: {
    setUser(state, user: User) {
      state.user = user;
    },
    setUserName(state, userName: string) {
      if (state.user) {
        state.user.userName = userName;
      } else {
        state.user = { userName, role: '' };
      }
    },
  },
  actions: {
    loginUser({ commit }, user: User) {
      // 这里可以进行登录请求的处理，成功后提交用户信息
      commit('setUser', user);
    },
    updateUserName({ commit }, userName: string) {
      commit('setUserName', userName);
    },
  },
  plugins: [createPersistedState()],
  modules: {
  }
})
