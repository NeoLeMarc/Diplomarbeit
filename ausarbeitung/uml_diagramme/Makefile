manvconnector_uml.pdf:  manvconnector_uml.mp
	mptopdf manvconnector_uml.mp
	mv manvconnector_uml-1.pdf manvconnector_uml.pdf 

manvconnector_uml_threads.pdf: manvconnector_uml_threads.mp
	mptopdf manvconnector_uml_threads.mp
	mv manvconnector_uml_threads-1.pdf manvconnector_uml_threads.pdf

manvconnector_uml_zigbit.pdf: manvconnector_uml_zigbit.mp
	mptopdf manvconnector_uml_zigbit.mp
	mv manvconnector_uml_zigbit-1.pdf manvconnector_uml_zigbit.pdf

manvconnector_uml_events.pdf: manvconnector_uml_events.mp
	mptopdf manvconnector_uml_events.mp
	mv manvconnector_uml_events-1.pdf manvconnector_uml_events.pdf


clean:
	rm -rf *.pdf *.ps

all: manvconnector_uml.pdf manvconnector_uml_threads.pdf manvconnector_uml_events.pdf
