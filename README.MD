
# Binancer (v1.1-alfa)
Binancer is a minimalist app that shows your spot wallet balance and all the assets in your Binance account.  Your account balance is updated regularly. The app currently uses USDT as the base currency and also EUR as the display currency.

### 🧰 App toolbox:
- Retrofit
- Room
- MVVM (in progress)
- Hilt
  
### 📚 Used libraries:
- [Lottie](https://lottiefiles.com/)
- [Glide](https://github.com/bumptech/glide)
- [Cryptocurrency Icons](https://github.com/spothq/cryptocurrency-icons)
- [MaterialPreferences](https://github.com/MFlisar/MaterialPreferences) - core & data store module 
- [Material backdrop](https://github.com/Semper-Viventem/Material-backdrop/)
- [StyleableToast](https://github.com/Muddz/StyleableToast)
- [Material Dialogs](https://github.com/afollestad/material-dialogs)
- [Chuck](https://github.com/jgilfelt/chuck) - debug

### 📜 TODOs
#### General
- WebSocket ticker 🔥
- refactor MVVM (separate ViewModels to each fragment)
- refactor ContentState handling
- You're offline - snack 🍫  
- portrain mode
- API IP weight used info bubble(mostly for debug)
- Theming properly ✨
- GridLayout view if enough space ✨
- Material design refactor ✨
- KnownSymbols DB -> Proto DataStore?
#### ApiCheckSplashActivity
- Check if API key valid 🔥
- QR scan
- SplasScreen crypto animation ✨
#### Trade/Transaction history
- caching to room and fetch only new data
- group items by date
- paging
#### Ticker
- add market cap and market position
#### Settings
- Show licences in app [LicensesDialog](https://github.com/PSDev/LicensesDialog)




