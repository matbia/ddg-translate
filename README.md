# ddg-translate

This repository contains a flexible Java library for accessing the proxied Microsoft's translation service provided by DuckDuckGo.
No need for API keys and dealing with rate-limits.

**Note: I don't vouch for the scalability and reliability of using the DuckDuckGo translator this way.**

## Description

The library uses web scraping together with a fake user agent to access the translator that normally appears in some search results pages.
The vqd token is extracted from HTML and is updated once a translation request fails with code 403.

## Installation

This library is not yet available in the Maven central repository, so the package needs to be manually compiled and imported into the project.

## Usage

A `Translator` class is used for accessing the web service endpoint by the end-user.
The constructors of this class use the received parameters to specify the desired input and output languages.
Also, a `Language` enum is recommended to use for managing the language codes used to instantiate it.
```java
Translator t = new Translator(Language.POLISH, Language.ENGLISH);
assertEquals(t.translate("Jan ma kota."), "John has a cat.");
```
A string language code can also be used instead, but this approach is more prone to error as passing an invalid string will result in IllegalArgumentException being thrown.
Check the `Language` enum for information about the corresponding code of each language.
```java
Translator t1 = new Translator("pl", "en");
// Same as
Translator t2 = new Translator(Language.POLISH.toString(), Language.ENGLISH.toString());
```
A single argument constructor can be used for input language detection, or for the same result set the first parameter of the constructor to Language.AUTO or an empty string.
This will let the translation service automatically detect the language in which given text is written in.
```java
// All of those translator objects are equivalent
Translator t1 = new Translator(Language.ENGLISH);
Translator t2 = new Translator(Language.AUTO, Language.ENGLISH);
Translator t3 = new Translator("en");
Translator t4 = new Translator("auto", "en");
```
Note that specifying the second parameter as automatic will result in IllegalArgumentException being thrown during the construction of the translator object.

## License
[GNU GPLv3](https://choosealicense.com/licenses/gpl-3.0/)
