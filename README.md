# Environ

Environ is a Clojure library for managing environment settings from a
number of different sources. It works well for applications following
the [12 Factor App](http://12factor.net/) pattern.

Currently, Environ supports four sources, resolved in the following
order:

1. A `.lein-env` file in the project directory
2. A `.boot-env` file on the classpath
3. Environment variables
4. Java system properties

The first two sources are set by the lein-environ and boot-environ
plugins respectively, and should not be edited manually.

The `.lein-env` file is populated with the content of the `:env` key
in the Leiningen project map.

The `.boot-env` file is populated by the `environ.boot/environ` Boot
task.


## Installation

Include the following dependency in your `project.clj` file:

```clojure
:dependencies [[environ "1.0.3"]]
```

If you want to be able to draw settings from the Leiningen project
map, you'll also need the following plugin:

```clojure
:plugins [[lein-environ "1.0.3"]]
```

If you are using the Boot toolchain, you may want to read and write
settings from build pipelines. In *build.boot*, add the dependency:

```clojure
:dependencies '[[boot-environ "1.0.3"]]
```

Then require the environ boot task.

```clojure
(require '[environ.boot :refer [environ]])
```


## Usage

Let's say you have an application that requires a database connection.
Often you'll need three different databases, one for development, one
for testing, and one for production.

Lets pull the database connection details from the key `:database-url`
on the `environ.core/env` map.

```clojure
(require '[environ.core :refer [env]])

(def database-url
  (env :database-url))
```

The value of this key can be set in several different ways. The most
common way during development is to use a local `profiles.clj` file in
your project directory. This file contained a map that is merged with
the standard `project.clj` file, but can be kept out of version
control and reserved for local development options.

```clojure
{:dev  {:env {:database-url "jdbc:postgres://localhost/dev"}}
 :test {:env {:database-url "jdbc:postgres://localhost/test"}}}
```

In this case we add a database URL for the dev and test environments.
This means that if you run `lein repl`, the dev database will be used,
and if you run `lein test`, the test database will be used.

In the case of Boot, you have the full flexibility of tasks and build
pipelines, meaning that all the following are valid:

```clojure
$ boot environ -e database-url=jdbc:postgres://localhost/dev repl
```

```clojure
(environ :env {:database-url "jdbc:postgres://localhost/dev"})
```

The latter form can be included in custom pipelines and `task-options!'.

The task also creates or updates a `.boot-env` file in the fileset.
This is useful for tasks that create their own pods like 
[boot-test](https://github.com/adzerk-oss/boot-test), which won't
see changes in the environ vars.

When you deploy to a production environment, you can make use of
environment variables, like so:

```bash
DATABASE_URL=jdbc:postgres://localhost/prod java -jar standalone.jar
```

Or use Java system properties:

```bash
java -Ddatabase.url=jdbc:postgres://localhost/prod -jar standalone.jar
```

Note that Environ automatically lowercases keys, and replaces the
characters "_" and "." with "-". The environment variable
`DATABASE_URL` and the system property `database.url` are therefore
both converted to the same keyword `:database-url`.


## License

Copyright © 2016 James Reeves

Distributed under the Eclipse Public License, the same as Clojure.
