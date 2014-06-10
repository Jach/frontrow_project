# Front Row Project

This is just an implementation of a simple API spec in Clojure
done as part of an interview process. It's available for others
(including myself in the future probably!) to look at and maybe
learn something, but its actual usage isn't really useful for
any purpose.

Run the server on port 3000 with `lein ring server-headless`,
additionally for the knowledge I created a main function to
start up a jetty server, so that simply `lein run` will also
run this project but on port 8321.

The server stores its data on disk under 'store.dat',
if you need to delete it make sure you shut down the server
and rm the file. (Or just send a DELETE request to /store.)

## License

Kevin Secretan claims no copyright over this code in the United States,
consider it in the public domain. For other countries where PD
may be more restrictive than licensed, consider it licensed
under the included copy of the MIT License.
