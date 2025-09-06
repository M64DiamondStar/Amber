# 🌸 Amber – Discord Bot

Amber is a private Discord bot built in **Kotlin** with [JDA](https://github.com/discord-jda/JDA) and [jda-ktx](https://github.com/minndevelopment/jda-ktx).  
It is designed to integrate with the [Effect Library API](https://github.com/M64DiamondStar/EffectLibrary-API), providing flows for asset submission, moderation, and approval.

⚠️ **Note:** This repository is public for transparency, but the bot itself is **not intended for public hosting**.  
The project is under MIT license so you can do with it whatever you want, but the bot itself is created to run in a very specific environment so it's pointless to host it yourself in this state.

---

## ✨ Features

- Useful/fun commands for users
- Interactive flows for **creating new assets** for the EffectMaster library
- Moderation tools:
  - Approve or reject submitted assets
  - Automatic cleanup of asset channels
- Integration with the **Effect Library API**
- Persistent session tracking via JSON config
- Fully coroutine-enabled event handling via `jda-ktx`

---

## 🛠️ Tech Stack

- **Kotlin** (JVM 21)
- **JDA** (Discord API wrapper)
- **jda-ktx** (Coroutine support for JDA)
- **Ktor** (handling API requests)
- **Kotlinx Serialization** (for configs and mappings)
- **PostgreSQL** (backing Effect Library API database)
- **Docker** (deployment)

---

## 📜 License
This project is licensed under the MIT License.
You are free to read, fork, and learn from this repository, but the hosted bot is not for public use.

<img width="512" height="512" alt="m64_dev ANIME pf_512" src="https://github.com/user-attachments/assets/51f4af4c-34fa-431e-baca-5cf2280816bc" />
