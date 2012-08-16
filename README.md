# Environ

Environ is a Clojure library for managing environment settings from a
number of different sources.

Currently, Environ supports two sources; environment variables, and
the Leiningen project map.


## Installation

Include the following dependency in your `project.clj` file:

```clojure
:dependencies [[environ "0.3.0"]]
```

If you want to be able to draw settings from the Leiningen project
map, you'll need the following plugin and hook:

```clojure
:plugins [[environ/environ.lein "0.3.0"]]
:hooks [environ.leiningen.hooks]
```

A good place to put this is in your `~/.lein/profiles.clj` file.


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


## License

Copyright © 2012 James Reeves

Distributed under the Eclipse Public License, the same as Clojure.
