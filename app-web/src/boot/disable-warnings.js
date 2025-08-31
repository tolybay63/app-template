export default ({ app }) => {
    // Отключить Vue предупреждения
    app.config.warnHandler = () => {};
};
