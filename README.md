<p align="center">
    <image alt="logo" width="320px" src="https://pbs.twimg.com/media/E5C4qWFXoAAUKNC?format=png&name=medium"><br/><br/>
</p>

# Cherie (ML app: coffee cherry quality prediction)

[![APK](https://img.shields.io/badge/APK-Latest_build-green.svg)](https://exp-shell-app-assets.s3.us-west-1.amazonaws.com/android/%40eloyjaws/cherie-8e1ff86339684d89a89b61af51529289-signed.apk)
[![Wireframes](https://img.shields.io/badge/Figma-Wireframes-blue.svg)]([LICENSE](https://www.figma.com/file/XL4VNEW2RJvyU93xZ9u72H/%F0%9F%93%B2Wireframes-for-mobile-UI?node-id=1%3A338))
[![apache-2.0](https://img.shields.io/badge/license-APACHE-red.svg)](LICENSE)

  
Naming is still in the works 😀 <br/>
Add a nomination here: (Cherie, Ripe)

This project aims to leverage ML in determining the quality score of freshly picked coffee cherries being brought in to the wet mills by farmers/collection-agents in Africa.

The quality score would be leveraged to implement differential pricing of coffee cherries (high quality/ripeness scores imply higher purchasing prices).

[Read more about the product here](PRODUCT)

## Structure
This is a monorepo containing several codebases
| Codebase                              |            Description             |
| :-----------------------------------: | :--------------------------------: |
| [cherry_serve](cherry_serve)          |          React Native App          |
| [ripe](ripe)                          |           Pix2Pix GAN V2           |
| [pix2pix](technoserve/pix2pix)        | Pix2Pix GAN for Image segmentation |
| [unet](technoserve/unet/model.py)     |  Unet GAN for Image segmentation   |
| [serialized model](ripe/model.tflite) |       TfLite Model (latest)        |

## Branches
- main -> don't touch, create a branch, work on your feature, and submit a PR

## How to run the mobile app locally

First of all, this project is currently in _very_ early stages of development, therefore these instructions may not be up to date.

> <em>For new React native developers</em><br/><br/>
> Install NodeJS, npm & yarn ([nvm](https://itnext.io/nvm-the-easiest-way-to-switch-node-js-environments-on-your-machine-in-a-flash-17babb7d5f1b) is a preferred way) <br/><br/>
> You can also follow the official React Native guide to getting setup: [HERE](https://reactnative.dev/docs/environment-setup)

This project uses the managed [expo](https://expo.io/) workflow (for now), so you'll need to install the expo-cli
```
yarn global add expo-cli
```

Clone the project

```bash
  git clone https://link-to-project
```

Go to the project directory

```bash
  cd my-project
```

Install dependencies

<sup>**We're using [Yarn](https://yarnpkg.com/) for this project, do not use npm for the following commands**</sup>

```
cd cherry_serve
yarn
```

You should now be all set to go, go ahead and run the dev server

```bash
yarn start
```

You now have a metro bundler running, you can start the app on Android or iOS

## Pre-requisites
If you're already familiar with JavaScript, React and React Native, then you'll be able to get moving quickly! If not, we highly recommend you to gain some basic knowledge first, then come back here when you're done.
<ol>
    <li>
        <a href="http://reactnativeexpress.com/">React Native Express (Sections 1 to 4)</a>
    </li>
    <li>
        <a href="https://reactjs.org/docs/hello-world.html">Main Concepts of React</a>
    </li>
    <li>
        <a href="https://reactjs.org/docs/hooks-intro.html">React Hooks</a>
    </li>
    <li>
        <a href="https://reactjs.org/docs/context.html">React Context (Advanced)</a>
    </li>
</ol>

## ML Models
There have been 4 different methods applied in the experiments to perform semantic image segmentation on coffee cherry images.
<ol>
<li>Plotting pixel colors in a 3d plot of HSV, RGB colorspaces to see if the images cluster automatically <a href="/technoserve/3dplots/README.md">Here</a></li>
<li>Using U-Net <a href="/technoserve/unet/model.py">Here</a></li>
<li>Using pix2pix GAN (tensorflow 1.4.1) <a href="/technoserve/pix2pix/commands.md">Here</a>
<br><em>Current implementation in repo may be out of date and has no saved weights.</em>
Check <a href="https://drive.google.com/file/d/1YJ4PT4kjAxACTlaunCvVdG-pGtE28rk6/view?ts=60b7d3fb">Google drive link</a> <br/>(Might require special access)
</li>
<li>Using a second pix2pix GAN (tensorflow + Keras) [Latest]</li>
</ol>


## Running Tests

There are currently no tests

```bash
  // TODO: Write tests
```

  

## Authors
- [Claire Chen](https://www.github.com/clch)
- [Eniola Ajiboye](https://www.github.com/eloyjaws)
- [Saksham Gakhar](https://www.github.com/sakshamg94)

  
## License

[apache-2.0](LICENSE)

  