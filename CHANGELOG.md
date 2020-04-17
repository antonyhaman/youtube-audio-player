# Changelog
All notable changes to this project will be documented in this file.

## [1.1.0]
- Fixed problem with exceeding google api quota limit by implementing YoutubeApiKeysProvider that stores multiple keys (in release version >10) and provides them to the app sequentially so each key's quota will be used evenly
- Implemented pagination for search results, before 1.1.0 search results were limited to 25

## [1.1.1]
- App is not more fixed to portrait orientation
- Slightly changed logic for sliding player panel elements transformations
- Moved to [Fastlane](https://gitlab.com/snippets/1895688) file structure
- Turned on Proguard
- Various tiny fixes and optimizations

## [1.1.2]
- Fixed numerous bugs and improved stability
- Moved to official Retrofit RxJava2 Adapter
- Bumped Exoplayer version
- Min SDK level is 21