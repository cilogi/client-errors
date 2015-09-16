# Spark framework starter

This is a simple starter example to show how to set up the [Spark Framework][1].
It demonstrates some of the basic functionality of the framework

## Requirements

To run it you need [Java 8][2], [Maven][3] and (optionally) [Intellij][4] for which there is a project set up. The
AppEngine plugin for Intellij only works with the Ultimate edition, so just use the Maven goals if you don't have it.

## Get started

This assumes that you have Java installed and can run Maven with the <code>mvn</code> command at a prompt.
To check this get up a command prompt and type <code>mvn --version</code> (note two hyphens).  You should
see a version number and other stuff about the Java version. You must have at least version 8 of Java, as
Spark uses the new closures.

If Maven is installed then simply type

    mvn clean package appengine:devserver

The first time this may take a while as a lot of dependencies need to be installed.  Future runs will be quicker.
There will be a lot of output finishing with:

    INFO: Dev App Server is now running

By default you are running from the address `http://localhost:8080` on the development server. All the paths
below are relative to this, which may differ for you if you change it.

Now open a web browser and go to: `/samples/hello`. You will see a message.

## Samples

All the samples run from the `Serve` class in package `com.cilogi.spark`.

### Static Files

The demo shows how to serve static files from the directory `public` under `resources`.

### Templates

You can see how to use [Mustache][5] templates at `/samples/template`.

### JSON

You can see some simple JSON output, served with the correct mime type, at `/samples/JSON`.

### Cookies

You can see a simple example of setting and getting a cookie at `/samples/cookie`.  The first
time you visit the page no cookie is set, the second time it is set.

### Form Handling

See the demo at `/samples/form.html`.  If you fill in the form you get redirected to a page
which displays the value you input.

## Running on App Engine

In order to run on AppEngine you need to sign up, register an application and upload.  Beyond
the scope of this document...

[1]: http://www.sparkjava.com/
[2]: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
[3]: http://maven.apache.org/download.cgi
[4]: http://www.jetbrains.com/idea/
[5]: http://mustache.github.io/