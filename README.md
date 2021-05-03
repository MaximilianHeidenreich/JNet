<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![GPLv3 License][license-shield]][license-url]

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/MaximilianHeidenreich/JNet.svg?style=flat-square
[contributors-url]: https://github.com/MaximilianHeidenreich/JNet/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/MaximilianHeidenreich/JNet?style=flat-square
[forks-url]: https://github.com/MaximilianHeidenreich/JNet/network
[stars-shield]: https://img.shields.io/github/stars/MaximilianHeidenreich/JNet?style=flat-square
[stars-url]: https://github.com/MaximilianHeidenreich/JNet/stargazers
[issues-shield]: https://img.shields.io/github/issues/MaximilianHeidenreich/JNet?style=flat-square
[issues-url]: https://github.com/MaximilianHeidenreich/JNet/issues
[license-shield]: https://img.shields.io/github/license/MaximilianHeidenreich/JNet?style=flat-square
[license-url]: https://github.com/MaximilianHeidenreich/JNet/blob/master/LICENSE

<!-- PROJECT HEADER -->
<br />
<p align="center">
  <a href="https://github.com/MaximilianHeidenreich/JNet">
    <img src="https://github.com/MaximilianHeidenreich/JNet/blob/master/assets/Icon-128.png?raw=true" alt="Project Logo" >
  </a>

<h2 align="center">JNet</h2>

  <p align="center">
    A small (opinionated) networking library using packets & callbacks that exposes a simple API.
    <br>
    <small>zero* - It uses <a href="https://logging.apache.org/log4j/2.x/">Log4J</a> for logging purposes</small>
    <br />
    <a href="#"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/MaximilianHeidenreich/JNet/issues">Report Bug</a>
    ·
    <a href="https://github.com/MaximilianHeidenreich/JNet/issues">Request Feature</a>
  </p>
</p>

<!-- TABLE OF CONTENTS -->
## Table of Contents

- [Table of Contents](#table-of-contents)
- [About The Project](#about-the-project)
    - [Features](#features)
- [Usage](#usage)
- [Benchmark](#benchmark)
- [Contributing](#contributing)
- [Contact](#contact)

<!-- ABOUT THE PROJECT -->
## About The Project

I created this project because I needed it for my [EnderSync]() project.
I just needed a basic networking library that supports packet sending and 
handling of response packets (see callbacks). Also, I did not want a super complicated API 
but something that is intuitive and easy to use. Because I found nothing, I created this library.

*Please note that this library is pretty opinionated! 
If you want something that is highly customizable, you'll have to use something else.*

### Features

- [x] Basic
    - [x] Server & Client abstraction
    - [x] Handle multiple named connections
    - [x] Callback functionality to handle packet responses
    - [x] Client authentication
- [x] API
    - [x] User friendly API.
- [x] Multithreaded

<br>

<!-- USAGE -->
## Usage

#### Add the dependency to your pom.xml
```xml
<project>
    ...
    <repositories>
        <repository>
            <id>maximilianheidenreich</id>
            <name>GitHub MaximilianHeidenreich Apache Maven Packages</name>
            <url>https://maven.pkg.github.com/maximilianheidenreich/*</url>
        </repository>
    </repositories>
    ...
    <dependencies>
        <dependency>
            <groupId>de.maximilian-heidenreich</groupId>
            <artifactId>jnet</artifactId>
            <version>2.2.0</version>
        </dependency>
    </dependencies>
    ....
</project>
```

-> Visit the `/examples` folder for usage examples.

<!-- BENCHMARK -->
## Benchmark

todo

<!-- CONTRIBUTING -->
## Contributing

Feel free to contribute to this project if you find something that is missing or can be optimized.
I want to retain the original vision of a simple yet usable library, so please keep that in mind when proposing new features.
If you do so, please follow the following steps:

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request


<!-- CONTACT -->
## Contact

Maximilian Heidenreich - github@maximilian-heidenreich.de

Project Link: [https://github.com/MaximilianHeidenreich/JNet](https://github.com/MaximilianHeidenreich/JNet)

Project Icon: [https://github.com/MaximilianHeidenreich/JNet/blob/master/assets/Icon-1024.png](https://github.com/MaximilianHeidenreich/JNet/blob/master/assets/Icon-1024.png)

<a href="https://www.buymeacoffee.com/maximili"><img src="https://img.buymeacoffee.com/button-api/?text=Buy me a coffee&emoji=&slug=maximili&button_colour=5F7FFF&font_colour=ffffff&font_family=Cookie&outline_colour=000000&coffee_colour=FFDD00"></a>
