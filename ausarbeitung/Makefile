Diplomarbeit.pdf: Diplomarbeit.dvi
	dvipdf Diplomarbeit.dvi 

Diplomarbeit.dvi: Titel.tex Vorwort.tex Danksagung.tex Abstract.tex Einleitung.tex Grundlagen.tex Stand_der_Technik.tex Entwurf.tex Praktische_Realisierung.tex Ergebnisse.tex Diskussion.tex Ausblick.tex
	latex  Diplomarbeit Titel.tex Vorwort.tex Danksagung.tex Abstect.tex Einleitung.tex Grundlagen.tex Stand_der_Technik.tex Entwurf.tex Praktische_Realisierung.tex Ergebnisse.tex Diskussion.tex Ausblick.tex Diplomarbeit.bib
	bibtex Diplomarbeit
	latex  Diplomarbeit Titel.tex Vorwort.tex Danksagung.tex Abstect.tex Einleitung.tex Grundlagen.tex Stand_der_Technik.tex Entwurf.tex Praktische_Realisierung.tex Ergebnisse.tex Diskussion.tex Ausblick.tex Diplomarbeit.bib
	latex  Diplomarbeit Titel.tex Vorwort.tex Danksagung.tex Abstect.tex Einleitung.tex Grundlagen.tex Stand_der_Technik.tex Entwurf.tex Praktische_Realisierung.tex Ergebnisse.tex Diskussion.tex Ausblick.tex Diplomarbeit.bib

clean:
	rm -f Diplomarbeit.pdf *.aux *.bbl *.dvi

all: Diplomarbeit.pdf
