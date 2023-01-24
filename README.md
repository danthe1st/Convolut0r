# Convolut0r
Convolut0r allows to manipulate images using (custom and preset) Convolutions.

## Convolutions
A Convolution is a multi-dimensional array (kernel) which is applied on images (or other kinds of multidimensional data) in a certain way.
For the output image, each pixel is calculated by multiplying pixels of the input image at the corresponding position with the kernel and summing up the results.
The kernel "slides" over the whole image producing the output image.

As an illustration, take a look at the following exemplary image:

[![Convolutions](https://user-images.githubusercontent.com/34687786/212144189-053015ca-23e5-4aba-b409-c30dafb5f349.png)](https://user-images.githubusercontent.com/34687786/212144189-053015ca-23e5-4aba-b409-c30dafb5f349.png)

Here, a 3x3 kernel "slides" over a 4x4 image where each position of the kernel in the image is marked by a rectangle.
For each pixel in the resulting (2x2) image, the kernel is applied to the specific area and the resulting pixel will be the sum of the all corresponding input pixels multiplied with the corresponding value of the kernel.

Aside from "normal" image processing, convolutions are widely used in Convoluted Neural Networks (CNNs) which are particularly good at working with high-dimensional data like images.

## Options

### Striding
Striding defines how fast the kernel "slides" over the image. A value of `1` means that it moves one pixel to the right each time.
Using a higher value than `1` for striding significantly decreases the resolution.

### Padding
Without a padding, the output image would have a lower resolution than the input image (see the exemplary image for before). It is possible to add some padding to the input image in order to increase the number of pixels in the output image.

# Download
Prebuilt artifacts can be downloaded [from GitHub Actions](https://github.com/danthe1st/Convolut0r/actions).

Select a completed workflow run, download the artifact, extract it and run the installer.

# Building the project

Once [Maven](https://maven.apache.org/) is installed, the project can be built and run using the command `mvn javafx:run`.