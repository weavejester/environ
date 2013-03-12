# Environ

Environ is a Clojure library for managing environment settings from a
number of different sources.

Currently, Environ supports three sources; environment variables, Java
system properties and the Leiningen project map using the lein-environ
plugin.


## Installation

Include the following dependency in your `project.clj` file:

```clojure
:dependencies [[environ "0.4.0-SNAPSHOT"]]
```

If you want to be able to draw settings from the Leiningen project
map, you'll also need the following plugin:

```clojure
:plugins [[lein-environ "0.4.0-SNAPSHOT"]]
```

A good place to put this is in your `profiles.clj` file.


## Usage

Let's say you have an application that requires an AWS access key and
secret key.

You can use Leiningen's profiles to add this information to your
development environment in your `~/.lein/profiles.clj` file, as it's
likely you'll be using the same AWS account for all development:

```clojure
{:user {:env {:aws-access-key "XXXXXXXXXXXXXXX"
              :aws-secret-key "YYYYYYYYYYYYYYYYYYYYYY"}}}
```

In your application, you can access these values through the
`environ.core/env` map:

```clojure
(use 'environ.core)

(def aws-creds
  {:access-key (env :aws-access-key)
   :secret-key (env :aws-secret-key)})
```

You'll likely also want to add `.lein-env` to your `.gitignore` file
(or the equivalent for your version control system).

When you deploy to a production environment, you can use standard
environment variables to configure the same settings.

```bash
AWS_ACCESS_KEY=XXXXXXXXXXXXXXX
AWS_SECRET_KEY=YYYYYYYYYYYYYYYYYYYYYY
```

Notice that the equivalent environment variables are uppercase, and
the "-" character has been replaced with "_".

You can also use Java system properties:

```
java -jar app-standalone.jar -Daws.access.key=XX -Daws.secret.key=YY
```

Note in this case that the "-" character has been replace with ".",
since this is the standard separator for system properties.


## License

Copyright © 2013 James Reeves

Distributed under the Eclipse Public License, the same as Clojure.
