NAME=Diplomarbeit
OTHER+=*.tex
OTHER+=*.bib
OTHER+=diagramme/*.*
OTHER+=uml_diagramme/*.*
OTHER+=Diplomarbeit.nls	
CLEAN_FILES+=*.aux
CLEAN_FILES+=*.nls

USE_PDFLATEX=1
VIEWPDF=acroread
MAKEIDX=makeindex
MAKEIDX_FLAGS="-o Diplomarbeit.nls -s nomencl.ist Diplomarbeit.nlo"

include /usr/share/latex-mk/latex.gmk

Diplomarbeit.nls: Diplomarbeit.nlo
	pdflatex  Diplomarbeit.tex *.tex
	makeindex -o Diplomarbeit.nls -s nomencl.ist Diplomarbeit.nlo
