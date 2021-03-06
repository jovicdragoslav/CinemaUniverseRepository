package servlets;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.AdminBean;
import beans.FilmBean;
import beans.LogovanjeBean;

/**
 * Servlet implementation class SiteServlet
 */
@WebServlet("/SiteServlet")
public class SiteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	FilmBean bean;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SiteServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		LogovanjeBean LGbean = (LogovanjeBean) request.getSession().getAttribute("LGbean");
		String kat = request.getParameter("inputpretraga");
		if (request.getParameter("logout") != null) {

			request.getSession().setAttribute("poruka", "");
			request.getSession().setAttribute("projekcije", null);
			request.getRequestDispatcher("/index.jsp").forward(request, response);

		} else if (request.getParameter("nazadProjekcije") != null) {

			request.getSession().setAttribute("poruka", "");
			request.getSession().setAttribute("projekcije", bean.pronadjiSveProjekcije());
			request.getRequestDispatcher("/site.jsp").forward(request, response);

		} else if (request.getParameter("prikazinajbolje") != null) {

			request.getSession().setAttribute("poruka", "");
			request.getSession().setAttribute("projekcije", LGbean.getTopProjekcije());
			request.getRequestDispatcher("/site.jsp").forward(request, response);

		} else if (request.getParameter("prikazi") != null) {

			request.getSession().setAttribute("poruka", "");
			request.getSession().setAttribute("projekcije", bean.pronadjiSveProjekcije());
			request.getRequestDispatcher("/site.jsp").forward(request, response);

		} else if (request.getParameter("pretraga") != null) {

			request.getSession().setAttribute("poruka", "");
			request.getSession().setAttribute("filmovi", bean.pretragaPoKategoriji(kat));
			request.getRequestDispatcher("/searchedMovies.jsp").forward(request, response);

		} else if (request.getParameter("pogledajF") != null) {

			request.getSession().setAttribute("poruka", "");
			int id = Integer.parseInt(request.getParameter("idFilma"));
			request.getSession().setAttribute("film", bean.pronadjiFilm(id));
			request.getRequestDispatcher("/film.jsp").forward(request, response);

		} else if (request.getParameter("zatvori") != null) {

			request.getSession().setAttribute("poruka", "");
			request.getRequestDispatcher("/site.jsp").forward(request, response);

		} else if (request.getParameter("reserve") != null) {

			try {
				int brk = Integer.parseInt(request.getParameter("brojkarata"));
				int idproj = Integer.parseInt(request.getParameter("idProjekcije"));
				int idkor = 0;
				try {
					idkor = LGbean.getLoggedUser().getKorisnikID();
				} catch (Exception e) {
				}
				if (idkor != 0) {
					if (LGbean.TryToInsertRezervacije(brk, idproj, idkor)) {
						request.getSession().setAttribute("poruka", "Uspesno ste rezervisali karte!");
					} else {
						request.getSession().setAttribute("poruka",
								"Rezervacija karata nije uspela! Molimo vas pokusajte ponovo!");
					}
				}
			} catch (Exception e) {
				request.getSession().setAttribute("poruka", "Rezervacija nije uspela! Molimo vas pokusajte ponovo!");
			} finally {
				request.getSession().setAttribute("projekcije", bean.pronadjiSveProjekcije());
				request.getRequestDispatcher("/site.jsp").forward(request, response);
			}

		} else if (request.getParameter("prodaj") != null) {
			try {
				int brk = Integer.parseInt(request.getParameter("brojkarata"));
				int idproj = Integer.parseInt(request.getParameter("idProjekcije"));
				if (bean.prodajKarte(brk, idproj)) {
					request.getSession().setAttribute("poruka", "Uspesno ste prodali karte!");
				} else {
					request.getSession().setAttribute("poruka",
							"Prodaja karata nije uspela! Molimo vas pokusajte ponovo!");
				}
			} catch (Exception e) {
				request.getSession().setAttribute("poruka", "Rezervacija nije uspela! Molimo vas pokusajte ponovo!");
			} finally {
				request.getSession().setAttribute("projekcije", bean.pronadjiSveProjekcije());
				request.getRequestDispatcher("/site.jsp").forward(request, response);
			}
		} else if (request.getParameter("sacuvajO") != null) {

			if (LGbean.getLoggedUser() != null) {
				int ocena;
				try {
					ocena = Integer.parseInt(request.getParameter("ocena"));
				} catch (NumberFormatException nfe) {
					ocena = 1;
				}
				int idproj = Integer.parseInt(request.getParameter("idProjekcije"));
				int idkor = LGbean.getLoggedUser().getKorisnikID();
				if (LGbean.sacuvajOcenu(idkor, idproj, ocena)) {
					request.getSession().setAttribute("poruka",
							"Uspesno ste ocenili projekciju!");
				} else {
					request.getSession().setAttribute("poruka",
							"Niste uspeli da ocenite projekciju! Molimo vas pokusajte ponovo!");
				}
			}
			request.getSession().setAttribute("projekcije", bean.pronadjiSveProjekcije());
			request.getRequestDispatcher("/site.jsp").forward(request, response);

		} else if (request.getParameter("sacuvajK") != null) {

			if (LGbean.getLoggedUser() != null && LGbean != null) {
				String tekst = request.getParameter("textKomentara");
				String datum = request.getParameter("datumKomentara");
				request.getSession().setAttribute("film", bean.dodajKomentar(tekst, datum,
						LGbean.getLoggedUser().getKorisnikID(), Integer.parseInt(request.getParameter("idFilmaK"))));
				request.getRequestDispatcher("/film.jsp").forward(request, response);
			} else {
				request.getRequestDispatcher("/film.jsp").forward(request, response);
			}
			request.getSession().setAttribute("poruka", "");

		} else if (request.getParameter("filtriraj") != null) {
			
			String brojMesta = request.getParameter("brojMesta");
			String cena = request.getParameter("cenaKarata");
			request.getSession().setAttribute("projekcije", bean.filtrirajProjekcije(brojMesta, cena));
			request.getRequestDispatcher("/site.jsp").forward(request, response);
			request.getSession().setAttribute("poruka", "");
			
		} else {
			
			request.getSession().setAttribute("poruka", "");
			request.getRequestDispatcher("/site.jsp").forward(request, response);
			
		}
	}

}
