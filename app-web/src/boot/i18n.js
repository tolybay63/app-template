import {boot} from 'quasar/wrappers'
import {createI18n} from 'vue-i18n'
import messages from 'src/i18n'

export default boot(({app}) => {
    const i18n = createI18n({
        locale: 'ru',
        legacy: false, // comment this out if not using Composition API
        globalInjection: true,
        messages,
        missing: (locale, key, vm) => {
            if (locale !== 'ru') {
                console.warn(`[i18n] Missing key '${key}' in locale '${locale}'`);
            }
        },
    })

    // Set i18n instance on app
    app.use(i18n)
})
