# Environ

Environ is a Clojure library for managing environment settings from a
number of different sources. It works well for applications following
the [12 Factor App](http://12factor.net/) pattern.

Currently, Environ supports three sources, resolved in the following
order:

1. A `.lein-env` file in the project directory
2. Environment variables
3. Java system properties

The first source can be set via the "lein-environ" Leiningen plugin,
which dumps the contents of the `:env` key in the project map into
that files.


## Installation

Include the following dependency in your `project.clj` file:

```clojure
:dependencies [[environ "0.4.0"]]
```

If you want to be able to draw settings from the Leiningen project
map, you'll also need the following plugin:

```clojure
:plugins [[lein-environ "0.4.0"]]
```


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
(or the equivalent for your version control system), if it isn't
covered by an existing rule.

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

These system properties will override any environment variables.


## License

Copyright © 2014 James Reeves

Distributed under the Eclipse Public License, the same as Clojure.
